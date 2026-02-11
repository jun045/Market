package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Cart cart = cartRepository.findByMemberId(user.getId()).orElse(null);
    private final CartItemQueryRepository cartItemQueryRepository;

        if(cart == null){
            return CartMapper.empty();
        }

        if(!cart.getMember().getId().equals(user.getId())){
            throw new IllegalArgumentException("자신의 장바구니만 조회 가능합니다");
        }

        //장바구니 상품 조회(페이지네이션 적용)
        Page<CartItemRaw> cartItemRaw = cartItemQueryRepository.cartItemRawList(member.getId(), cartInfo.cartId(), pageable);
        List<CartItemRaw> cartItems = cartItemRaw.getContent();
        long totalElements = cartItemRaw.getTotalElements();

        List<CartItemResponse> cartItemResonseList = CartMapper.toCartItemResonseList(cartItems);

        return CartMapper.toCartResponse(cart, cartItemResonseList);

    }

}
