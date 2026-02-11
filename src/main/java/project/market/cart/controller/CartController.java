package project.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.market.cart.dto.CartResponse;
import project.market.cart.service.CartService;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;

    @GetMapping("me/carts")
    public CartResponse getCart (@AuthenticationPrincipal (expression = "member") Member member){
    @GetMapping("api/v1/me/carts")

        return cartService.getCart(member);
    }

}
