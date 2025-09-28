package project.market.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CreateCartItemRequest;
import project.market.cart.entity.CartItem;
import project.market.cart.service.CartService;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;


}
