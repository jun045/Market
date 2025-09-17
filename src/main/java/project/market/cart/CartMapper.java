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
}
