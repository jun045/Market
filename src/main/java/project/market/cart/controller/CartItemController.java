package project.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.dto.UpdateCartItemRequest;
import project.market.cart.service.CartItemService;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@RestController
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("api/v1/me/cart/items")
    public CartItemResponse createCartItem (@AuthenticationPrincipal (expression = "member") Member member,
                                            @RequestBody CreateCartItemRequest request){

        return cartItemService.create(member, request);
    }

    @PatchMapping("api/v1/me/cart/items/{cartItemId}")
    public CartItemResponse updateCartItem (@AuthenticationPrincipal (expression = "member") Member member,
                                            @PathVariable Long cartItemId,
                                            @RequestBody UpdateCartItemRequest request){

        return cartItemService.update(member, cartItemId, request);
    }

    //장바구니 아이템 삭제
    @DeleteMapping("api/v1/me/cart/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem (@AuthenticationPrincipal (expression = "member") Member member,
                                                @PathVariable Long cartItemId){

        cartItemService.delete(member, cartItemId);

        return ResponseEntity.ok().build();
    }
}
