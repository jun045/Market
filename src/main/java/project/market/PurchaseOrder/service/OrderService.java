package project.market.PurchaseOrder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.OrderItem.OrderItem;
import project.market.OrderItem.OrderItemMapper;
import project.market.OrderItem.dto.OrderItemDetailResponse;
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.PurchaseOrder.OrderMapper;
import project.market.PurchaseOrder.OrderStatus;
import project.market.PurchaseOrder.PurchaseOrderRepository;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.PurchaseOrder.dto.OrderListResponse;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PurchaseOrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final VariantRepository variantRepository;

    //권한 체크 메서드
    private Member requireUser(Member member) {
        return memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));
    }

    private void requireAdmin(Member member) {
        Member user = requireUser(member);

        if (!user.getRole().equals(Role.SELLER)) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다");
        }
    }

    private PurchaseOrder requireUserOrder(Member member, Long orderId) {
        Member user = requireUser(member);

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음"));

        if (!order.getMember().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 주문만 조회 / 취소 요청 가능");
        }
        return order;
    }

    //주문 생성(상품칸에서 바로 구매)
    @Transactional
    public OrderDetailResponse createOrder(Member member, CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("주문 요청이 비어있습니다");
        }

        Member user = requireUser(member);
        if (user.getRole() == Role.SELLER) {
            throw new IllegalStateException("관리자는 주문할 수 없습니다.");
        }
        if (request.orderItems() == null || request.orderItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목 비어있음");
        }
        if (request.usedPoint() < 0) {
            throw new IllegalArgumentException("사용포인트는 음수일 수 없음");
        }

        //주문 생성(orderItems는 addOrderItem에서)
        PurchaseOrder order = PurchaseOrder.builder()
                .orderStatus(OrderStatus.CREATED)
                .member(user)
                .usedPoint(request.usedPoint())
                .build();

        // 1) 동일 variantId에 대해 요청된 총 수량을 집계 (한 variant에 대해 여러 라인이 올 수 있으므로)
        Map<Long, Integer> aggregatedQty = new HashMap<>();
        for (var itemRequest : request.orderItems()) {
            if (itemRequest == null || itemRequest.productVariantId() == null) {
                throw new IllegalArgumentException("상품 옵션 정보 올바르지 않음");
            }
            if (itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("상품 옵션 개수는 1개 이상이어야 함");
            }
            aggregatedQty.merge(itemRequest.productVariantId(), itemRequest.quantity(), Integer::sum);
        }

        // 2) 집계된 variantId들 로드하고 총 필요 수량으로 재고 검증 (각 variant에 대해 1회만 DB 조회)
        Map<Long, ProductVariant> variantsById = new HashMap<>();
        for (Map.Entry<Long, Integer> e : aggregatedQty.entrySet()) {
            Long variantId = e.getKey();
            Integer totalNeeded = e.getValue();
            ProductVariant variant = variantRepository.findById(variantId)
                    .orElseThrow(() -> new IllegalArgumentException("상품 옵션 없음: " + variantId));
            // 도메인 검증 메서드 사용
            variant.validateStockOrThrow(totalNeeded);
            variantsById.put(variantId, variant);
        }

        // 3) 실제 요청 라인 단위로 재고 차감 + OrderItem 추가 (variantsById에서 재사용)
        for (var itemRequest : request.orderItems()) {
            Long variantId = itemRequest.productVariantId();
            ProductVariant variant = variantsById.get(variantId);
            if (variant == null) {
                throw new IllegalArgumentException("상품 옵션 없음: " + variantId);
            }
            // 실제 차감 (각 라인 수량만큼 차감)
            variant.decreaseStock(itemRequest.quantity());
            // 도메인 레벨에서 OrderItem 병합(같은 옵션이면 수량 증가) 처리
            order.addOrderItem(variant, itemRequest.quantity());
        }

        order.recalculateOrderTotal();
        orderRepository.save(order);

        List<OrderItemDetailResponse> itemDtos = order.getOrderItems().stream()
                .map(OrderItemMapper::toDetailResponse)
                .toList();

        return OrderMapper.toDetailResponse(order, itemDtos);
    }

    //주문 전체 조회 - 관리자
    @Transactional(readOnly = true)
    public List<OrderListResponse> adminFindAllOrder(Member member) {
        requireAdmin(member);

        List<PurchaseOrder> purchaseOrders = orderRepository.findAll();

        List<OrderListResponse> responses = purchaseOrders.stream()
                .map(OrderMapper::toListResponse)
                .toList();

        return responses;
    }

    //주문 전체 조회 - 사용자
    @Transactional(readOnly = true)
    public List<OrderListResponse> userFindAllOrder(Member member) {
        Member user = requireUser(member);

        List<PurchaseOrder> orders = orderRepository.findByMemberId(user.getId());

        return orders.stream()
                .map(OrderMapper::toListResponse)
                .toList();
    }

    //주문 상세 조회 - 관리자
    @Transactional(readOnly = true)
    public OrderDetailResponse adminFindOrder(Member member, Long orderId) {
        requireAdmin(member);

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음"));

        List<OrderItemDetailResponse> itemDtos = order.getOrderItems().stream()
                .map(OrderItemMapper::toDetailResponse)
                .toList();

        return OrderMapper.toDetailResponse(order, itemDtos);
    }

    //주문 상세 조회 - 사용자
    @Transactional(readOnly = true)
    public OrderDetailResponse userFindOrder(Member member, Long orderId) {
        PurchaseOrder order = requireUserOrder(member, orderId);

        List<OrderItemDetailResponse> itemDtos = order.getOrderItems().stream()
                .map(OrderItemMapper::toDetailResponse)
                .toList();

        return OrderMapper.toDetailResponse(order, itemDtos);
    }

    //주문 상태 수정 - 관리자
    @Transactional
    public void adminUpdateOrderStatus(Member member,
                                       Long orderId,
                                       OrderStatus newStatus) {
        requireAdmin(member);

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음:" + orderId));

        //배송시작 or 배송 완료일때
        if (order.getOrderStatus() == OrderStatus.SHIPPED
                || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 시작 또는 배송 완료 주문은 수정 불가");
        }
        order.setOrderStatus(newStatus);
    }

    //주문 취소 요청 - 사용자
    @Transactional
    public void userRequestCancelOrder(Member member, Long orderId) {
        PurchaseOrder order = requireUserOrder(member, orderId);

        //주문 생성,결제 완료만 취소 가능
        if (order.getOrderStatus() != OrderStatus.CREATED &&
                order.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("취소 요청이 가능한 상태가 아님");
        }
        //주문 취소요청 상태로 변경
        order.setOrderStatus(OrderStatus.CANCEL_REQUESTED);
    }

    //취소 승인 - 관리자
    @Transactional
    public void approveCancelRequest(Member member, Long orderId) {
        requireAdmin(member);

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음:" + orderId));

        if (order.getOrderStatus() != OrderStatus.CANCEL_REQUESTED) {
            throw new IllegalArgumentException("취소 요청 상태 아님. 현재 상태:" + order.getOrderStatus());
        }
        //TODO:
        // 1. PG사 호출 환불처리
        // 2. 사용 포인트 복구
        // 3. 재고 복구

        order.cancel();
    }

    //주문 소프트 삭제 - 관리자만 가능
    @Transactional
    public void deleteOrder(Member member, Long orderId) {
        requireAdmin(member);

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음:" + orderId));

        //배송시작 or 배송 완료일때
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송이 시작됐거나 완료 주문은 취소할 수 없음");
        }
        order.deletedOrder();
    }
}
