package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.CartItemMapper;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
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

        Product product = productRepository.findById(request.productId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
        );

        OptionVariant optionVariant = optionVariantRepository.findById(request.optionVariantId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 옵션입니다.")
        );

        //Cart가 없다면 최초 생성
        Cart cart = cartRepository.findByMemberId(user.getId()).orElseGet(
                () -> cartRepository.save(Cart.createCart(user))
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

        return CartItemMapper.toResponse(cartItem);
    }

}
