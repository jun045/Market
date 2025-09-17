package project.market.cart;

import project.market.cart.dto.CartItemResponse;
import project.market.cart.dto.CartResponse;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;

import java.util.List;

public class CartMapper {

    public static CartItemResponse toResponse (CartItem cartItem){

        Integer salePrice = cartItem.getOptionVariant().getSalePrice();
        Integer quantity = cartItem.getQuantity();
        Integer itemPrice = salePrice * quantity;

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getProductName())
                .thumb(cartItem.getProduct().getThumbnail())
                .optionVariantId(cartItem.getOptionVariant().getId())
                .optionSummary(cartItem.getOptionVariant().getOptionSummary())
                .quantity(quantity)
                .salePrice(salePrice)
                .itemPrice(itemPrice)
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }

    public static CartResponse toCartResponse (Cart cart){

        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream().map(
                        cartItem -> CartItemResponse.builder()
                                .id(cartItem.getId())
                                .productId(cartItem.getProduct().getId())
                                .productName(cartItem.getProduct().getProductName())
                                .thumb(cartItem.getProduct().getThumbnail())
                                .optionVariantId(cartItem.getOptionVariant().getId())
                                .optionSummary(cartItem.getOptionVariant().getOptionSummary())
                                .quantity(cartItem.getQuantity())
                                .salePrice(cartItem.getOptionVariant().getSalePrice())
                                .itemPrice(cartItem.getQuantity() * cartItem.getOptionVariant().getSalePrice())
                                .createdAt(cartItem.getCreatedAt())
                                .updatedAt(cartItem.getUpdatedAt())
                                .build())
                        .toList();

        int totalPrice = cartItemResponses.stream().mapToInt(
                CartItemResponse::itemPrice
        ).sum();

        int totalQuantity = cartItemResponses.stream().mapToInt(
                CartItemResponse::quantity
        ).sum();


        return CartResponse.builder()
                .cartId(cart.getId())
                .cartItemResponses(cartItemResponses)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .updatedAt(cart.getUpdatedAt())
                .build();


    }
}
