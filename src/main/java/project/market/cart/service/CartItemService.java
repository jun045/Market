package project.market.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.CartMapper;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.dto.UpdateCartItemRequest;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.product.OptionVariant;
import project.market.product.OptionVariantRepository;
import project.market.product.Product;
import project.market.product.ProductRepository;

@RequiredArgsConstructor
@Service
public class CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OptionVariantRepository optionVariantRepository;
    private final MemberRepository memberRepository;

    public CartItemResponse create (Member member, CreateCartItemRequest request){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다.")
        );

        //Cart가 없다면 최초 생성
        Cart cart = cartRepository.findByMemberId(user.getId()).orElseGet(
                () -> cartRepository.save(Cart.createCart(user))
        );

        Product product = productRepository.findById(request.productId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
        );

        //이미 장바구니에 넣은 상품과 옵션을 다시 넣으면
        //장바구니에 또 상품을 추가하지 않고 기존에 넣어둔 상품과 옵션의 수량만 증가시킴
        CartItem existingItem = cartItemRepository.findByCartIdAndOptionVariantId(cart.getId(), request.optionVariantId()).orElse(null);

        if(existingItem != null){
            existingItem.increaseQuantity(request.quantity());
            return CartMapper.toResponse(existingItem);
        }

        OptionVariant optionVariant = optionVariantRepository.findById(request.optionVariantId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 옵션입니다.")
        );

        CartItem cartItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .optionVariant(optionVariant)
                .quantity(request.quantity())
                .build();

        //추가된 cartItem cart에 반영(편의 매서드)
        cart.addCart(cartItem);
        cartItemRepository.save(cartItem);

        return CartMapper.toResponse(cartItem);
    }

    @Transactional
    public CartItemResponse update (Member member, Long cartItemIdId, UpdateCartItemRequest request){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다.")
        );

        CartItem cartItem = cartItemRepository.findById(cartItemIdId).orElseThrow(
                () -> new IllegalArgumentException("장바구니에 등록되지 않은 상품입니다.")
        );

        if(!cartItem.getCart().getMember().getId().equals(user.getId())){
            throw new IllegalArgumentException("자신의 장바구니에 담긴 상품만 수정할 수 있습니다.");
        }

        //장바구니 상품 수량 수정
        cartItem.setCartItemQuantity(request.quantity());

        return CartMapper.toResponse(cartItem);
    }

}
