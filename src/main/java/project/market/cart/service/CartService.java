package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.CartMapper;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CartResponse;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    public CartResponse getCart (Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다")
        );

        Cart cart = cartRepository.findByMemberId(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("자신의 장바구니만 조회할 수 있습니다")
        );

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());

        List<CartItemResponse> cartItemResonseList = CartMapper.toCartItemResonseList(cartItems);

        return CartMapper.toCartResponse(cart, cartItemResonseList);

    }

}
