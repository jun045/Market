package project.market.PurchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.OrderItem.OrderItem;
import project.market.OrderItem.OrderItemRepository;
import project.market.OrderItem.dto.CreateOrderItemRequest;
import project.market.OrderItem.dto.OrderItemResponse;
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderResponse;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PurchaseOrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final VariantRepository variantRepository;

    //주문 생성,수정,조회, 상태관리,포인트관리, 주문데이터 검증,외부연동 인터페이스, 트랜잭션 관리

    //주문 생성
    public OrderResponse createOrder(CreateOrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));

        //orderitem 주문항목 리스트 생성
        List<OrderItem> orderItems = request.orderItems().stream()
                .map(items -> {
                    ProductVariant variant = variantRepository.findById(items.productVariantId())
                            .orElseThrow(() -> new IllegalArgumentException("상품 옵션 없음: " + items.productVariantId()));

                    return OrderItem.builder()
                            .productVariant(variant)
                            .quantity(items.quantity())
                            .unitPrice((int) variant.calculateFinalPrice())
                            .build();
                }).toList();

        //주문 본체 생성
        PurchaseOrder order = PurchaseOrder.builder()
                .orderStatus(OrderStatus.CREATED)
                .member(member)
                .usedPoint(request.usedPoint())
                .orderItems(orderItems)
                .build();

        //양방향
        orderItems.forEach(item -> item.assignOrder(order));
        //총 금액 계산
        order.recalculateOrderTotal();
        orderRepository.save(order);

        //orderitem -> dto 변환
        //주문 엔티티가 주문항목 리스트를 관리하는 중심 역할이므로 order에서 orderitems 불러옴
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(oi -> new OrderItemResponse(
                        oi.getId(),
                        oi.getProductVariant().getId(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.calculateTotalPrice()))
                .toList();

        //최종 반환
        return new OrderResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getOrderTotalPrice(),
                order.getUsedPoint(),
                order.getEarnPoint(),
                order.getPayAmount(),
                itemResponses
        );
    }

    //주문 조회 - 관리자
    @Transactional(readOnly = true)
    public OrderResponse adminFindOrder(Member member,Long orderId) {
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));

        if (!user.getRole().equals(Role.SELLER)) {
            throw new IllegalStateException("관리자 권한이 필요합니다");
        }

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음"));

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(oi -> new OrderItemResponse(
                        oi.getId(),
                        oi.getProductVariant().getId(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.calculateTotalPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getOrderTotalPrice(),
                order.getUsedPoint(),
                order.getEarnPoint(),
                order.getPayAmount(),
                itemResponses
        );
    }

    //주문 조회 - 사용자(본인것만 조회 가능)
    @Transactional(readOnly = true)
    public OrderResponse userFindOrder(Member member, Long orderId) {
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음"));

        if (!order.getMember().getId().equals(user.getId())) {
            throw new IllegalStateException("본인 주문만 조회할 수 있습니다");
        }

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(oi -> new OrderItemResponse(
                        oi.getId(),
                        oi.getProductVariant().getId(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.calculateTotalPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getOrderTotalPrice(),
                order.getUsedPoint(),
                order.getEarnPoint(),
                order.getPayAmount(),
                itemResponses
        );
    }

    //주문 수정 - 관리자
    @Transactional
    public void adminUpdateOrderStatus(Member member, Long orderId, OrderStatus newStatus) {
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));

        if (!user.getRole().equals(Role.SELLER)) {
            throw new IllegalStateException("관리자 권한이 필요합니다");
        }

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음:" + orderId));

        //배송시작 or 배송 완료일때
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 시작 또는 배송 완료 주문은 수정할 수 없음");
        }
        order.setOrderStatus(newStatus);
    }

    //주문 수정 - 사용자(취소 요청만 가능)
    @Transactional
    public void userRequestCancelOrder(Member member, Long orderId){
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(()-> new IllegalArgumentException("로그인이 필요합니다"));

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("주문 찾을 수 없음"));

        if (!order.getMember().getId().equals(user.getId())){
            throw new IllegalStateException("본인 주문만 취소 요청 가능");
        }

        //주문 생성,결제 완료만 취소 가능
        if (order.getOrderStatus() != OrderStatus.CREATED &&
            order.getOrderStatus() != OrderStatus.PAID){
            throw new IllegalStateException("취소 요청이 가능한 주문 상태가 아님");
        }
        //주문 취소요청 상태로 변경
        order.setOrderStatus(OrderStatus.CANCEL_REQUESTED);
    }

    //주문 삭제 - 관리자만 가능
    @Transactional
    public void cancelOrder(Member member, Long orderId) {
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));

        if (!user.getRole().equals(Role.SELLER)) {
            throw new IllegalStateException("관리자 권한이 필요합니다");
        }

        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 찾을 수 없음:" + orderId));

        //배송시작 or 배송 완료일때
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송이 시작됐거나 완료 주문은 취소할 수 없음");
        }
        order.deletedOrder();
    }
}
