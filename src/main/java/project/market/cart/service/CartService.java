package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.market.cart.CartMapper;
import project.market.cart.dto.CartResponse;
import project.market.cart.entity.Cart;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;


    //장바구니 조회
    public CartResponse get (Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("로그인이 필요합니다.")
        );

        Cart cart = cartRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("자신의 장바구니만 조회할 수 있습니다.")
        );

        return CartMapper.toCartResponse(cart);
    }
}
