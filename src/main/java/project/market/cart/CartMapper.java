package project.market.cart;

import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CartResponse;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;

import java.util.List;

public class CartMapper {

    public static CartItemResponse toCartItemResponse (CartItem cartItem){

        int quantity = cartItem.getQuantity();
        long salePrice = cartItem.getProductVariant().calculateFinalPrice();
        long itemPrice = salePrice * quantity;

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getProductName())
                .thumb(cartItem.getProduct().getThumbnail())
                .variantId(cartItem.getProductVariant().getId())
                .options(cartItem.getProductVariant().getOptions())
                .quantity(quantity)
                .salePrice(salePrice)
                .itemPrice(itemPrice)
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }

    public static CartResponse toCartResponse (Cart cart, List<CartItemResponse> cartItemResponses){

        long totalPrice = cartItemResponses.stream().mapToLong(
                CartItemResponse::itemPrice).sum();

        int totalQuantity = cartItemResponses.stream().mapToInt(
                CartItemResponse::quantity).sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .cartItemResponses(cartItemResponses)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public static List<CartItemResponse> toCartItemResonseList (List<CartItem> cartItems){

        return cartItems.stream().map(
                cartItem -> CartItemResponse.builder()
                        .id(cartItem.getId())
                        .productId(cartItem.getId())
                        .productName(cartItem.getProduct().getProductName())
                        .thumb(cartItem.getProduct().getThumbnail())
                        .variantId(cartItem.getProductVariant().getId())
                        .options(cartItem.getProductVariant().getOptions())
                        .quantity(cartItem.getQuantity())
                        .salePrice(cartItem.getProductVariant().calculateFinalPrice())
                        .itemPrice(cartItem.getQuantity()*cartItem.getProductVariant().calculateFinalPrice())
                        .createdAt(cartItem.getCreatedAt())
                        .updatedAt(cartItem.getUpdatedAt())
                        .build()
        ).toList();
    }
}
