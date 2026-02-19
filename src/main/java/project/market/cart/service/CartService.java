package project.market.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.market.cart.CartAssembler;
import project.market.cart.CartMapper;
import project.market.cart.dto.*;
import project.market.cart.repository.CartItemQueryRepository;
import project.market.cart.repository.CartQueryRepository;
import project.market.member.Entity.Member;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartQueryRepository cartQueryRepository;
    private final CartItemQueryRepository cartItemQueryRepository;
    private final CartAssembler cartAssembler;

    public GetCartResponse getCart (Member member, Pageable pageable){

        //장바구니 조회(없으면 빈 장바구니 반환)
        CartRaw cartInfo = cartQueryRepository.cartInfo(member.getId());
        if(cartInfo == null){
            return CartMapper.empty(pageable);
        }

        //장바구니 상품 조회(페이지네이션 적용)
        Page<CartItemRaw> cartItemRaw = cartItemQueryRepository.cartItemRawList(member.getId(), cartInfo.cartId(), pageable);
        List<CartItemRaw> cartItems = cartItemRaw.getContent();
        long totalElements = cartItemRaw.getTotalElements();

        //장바구니 상품의 총액과 총수량 계산
        CartTotalRaw cartTotalRaw = cartItemQueryRepository.cartTotals(cartInfo.cartId(), member.getId());

        return cartAssembler.toGetCartResponse(cartInfo, cartItems, cartTotalRaw, totalElements, pageable);

    }

}
