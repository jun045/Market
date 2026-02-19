package project.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.market.cart.dto.GetCartResponse;
import project.market.cart.service.CartService;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;

    @GetMapping("/api/v1/me/carts")
    public GetCartResponse getCart (@AuthenticationPrincipal (expression = "member") Member member,
                                    @RequestParam(defaultValue = "1") int pageNumber,
                                    @RequestParam (defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(pageNumber -1, size);
        return cartService.getCart(member, pageable);
    }

}
