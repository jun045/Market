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
import project.market.PurchaseOrder.repository.PurchaseOrderRepository;
import project.market.PurchaseOrder.dto.CreateCartOrderRequest;
import project.market.PurchaseOrder.dto.OrderDetailResponse;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Role;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartOrderService {
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final VariantRepository variantRepository;
    private final PurchaseOrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Authentication principal(또는 외부에서 전달된 Member)이
     * - id, role 등 필요한 정보를 충분히 가지고 있으면 재조회 없이 사용하고,
     * - 그렇지 않으면 DB에서 재조회
     */
    private Member resolveMember(Member principal) {
        if (principal == null || principal.getId() == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        return memberRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다"));
    }

    //장바구니에서 주문
    @Transactional
    public OrderDetailResponse orderCartItems(Member member, CreateCartOrderRequest request) {
        if (request == null){
            throw new IllegalArgumentException("요청이 비어있습니다");
        }

        Member user = resolveMember(member);

        if (user.getRole().equals(Role.SELLER)) {
            throw new IllegalStateException("관리자는 주문할 수 없습니다.");
        }

        Cart cart = cartRepository.findByMemberId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다"));


        //장바구니 안 요청한 상품만 필터링
        List<Long> requestedIds = request.cartItemIds();
        if (requestedIds ==null || requestedIds.isEmpty()){
            throw new IllegalArgumentException("주문할 상품을 선택하세요");
        }

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> requestedIds.contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalStateException("선택된 장바구니 아이템이 없습니다");
        }

        //주문 생성
        PurchaseOrder order = PurchaseOrder.builder()
                .orderStatus(OrderStatus.CREATED)
                .member(user)
                .build();

        //검증 - 주문 아이템에 추가 - 재고 차감 - 저장 순
        for (CartItem cartItem : selectedItems){
            ProductVariant variant = cartItem.getProductVariant();
            if (variant == null) {
                throw new IllegalArgumentException("장바구니 아이템의 상품 옵션이 올바르지 않음");
            }

            int quantity = cartItem.getQuantity();
            if (quantity <=0){
                throw new IllegalArgumentException("상품 수량 올바르지 않음");
            }

            if (!variant.isSaleAvailable()){
                throw new IllegalArgumentException("현재 구매할 수 없는 옵션입니다");
            }

            variant.validateStockOrThrow(quantity); //재고 검증

            order.addOrderItem(variant,quantity);
        }

        for (CartItem cartItem : selectedItems){
            ProductVariant variant = cartItem.getProductVariant();
            variant.decreaseStock(cartItem.getQuantity());
        }

        order.recalculateOrderTotal();
        orderRepository.save(order);

        cartItemRepository.deleteAll(selectedItems);

        //Dto 변환 및 반환
        List<OrderItemDetailResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemMapper::toDetailResponse)
                .toList();

        return OrderMapper.toDetailResponse(order,itemResponses);
    }
}
