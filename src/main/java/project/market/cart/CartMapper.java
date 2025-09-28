package project.market.cart;

import project.market.cart.dto.CartItemResponse;
import project.market.cart.entity.CartItem;

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
}
