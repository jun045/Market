package project.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.market.cart.dto.CartResponse;
import project.market.cart.repository.CartRepository;
import project.market.cart.service.CartService;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;


    //장바구니 조회
    @GetMapping("api/v1/me/cart")
    public CartResponse getCart (@AuthenticationPrincipal (expression = "member")Member member){

        return cartService.get(member);
    }
}
