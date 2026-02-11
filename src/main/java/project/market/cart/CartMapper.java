package project.market.cart;

import org.springframework.data.domain.Pageable;
import project.market.PageResponse;
import project.market.cart.dto.*;
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

//    public static CartResponse toCartResponse (Cart cart, List<CartItemResponse> cartItemResponses){
//
//        long totalPrice = cartItemResponses.stream().mapToLong(
//                CartItemResponse::itemPrice).sum();
//
//        int totalQuantity = cartItemResponses.stream().mapToInt(
//                CartItemResponse::quantity).sum();
//
//        return CartResponse.builder()
//                .cartId(cart.getId())
//                .cartItemResponses(cartItemResponses)
//                .totalPrice(totalPrice)
//                .totalQuantity(totalQuantity)
//                .updatedAt(cart.getUpdatedAt())
//                .build();
//    }
//
//    public static List<CartItemResponse> toCartItemResonseList (List<CartItem> cartItems){
//
//        return cartItems.stream().map(
//                cartItem -> CartItemResponse.builder()
//                        .id(cartItem.getId())
//                        .productId(cartItem.getId())
//                        .productName(cartItem.getProduct().getProductName())
//                        .thumb(cartItem.getProduct().getThumbnail())
//                        .variantId(cartItem.getProductVariant().getId())
//                        .options(cartItem.getProductVariant().getOptions())
//                        .quantity(cartItem.getQuantity())
//                        .salePrice(cartItem.getProductVariant().calculateFinalPrice())
//                        .itemPrice(cartItem.getQuantity()*cartItem.getProductVariant().calculateFinalPrice())
//                        .createdAt(cartItem.getCreatedAt())
//                        .updatedAt(cartItem.getUpdatedAt())
//                        .build()
//        ).toList();
//    }

    public static List<CartItemResponse> toCartItemResponseList (List<CartItemRaw> cartItemRaw){

        return cartItemRaw.stream().map(
                raw -> CartItemResponse.builder()
                        .id(raw.id())
                        .productId(raw.productId())
                        .productName(raw.productName())
                        .thumb(raw.thumb())
                        .variantId(raw.variantId())
                        .options(raw.options())
                        .quantity(raw.quantity())
                        .salePrice((raw.listPrice() + raw.extraCharge() - (raw.discountPrice() != null ? raw.discountPrice() : 0)))
                        .itemPrice((raw.listPrice() + raw.extraCharge() - (raw.discountPrice() != null ? raw.discountPrice() : 0)) * raw.quantity())
                        .createdAt(raw.createdAt())
                        .updatedAt(raw.updatedAt())
                        .build()
        ).toList();
    }

    public static CartResponse toCartResponse (CartRaw cartRaw, CartTotalRaw cartTotalRaw){
//
//        long totalPrice = cartItemResponses.stream().mapToLong(
//                CartItemResponse::salePrice
//        ).sum();
//
//        int totalQuantity = cartItemResponses.stream().mapToInt(
//                CartItemResponse::quantity
//        ).sum();



        return CartResponse.builder()
                .cartId(cartRaw.cartId())
                .totalPrice(cartTotalRaw.totalPrice())
                .totalQuantity(cartTotalRaw.totalQuantity())
                .updatedAt(cartRaw.updatedAt())
                .build();
    }

    public static GetCartResponse toGetCartResponse (CartResponse cartResponse, PageResponse<CartItemResponse> cartItems){

        return GetCartResponse.builder()
                .cartResponse(cartResponse)
                .cartItems(cartItems)
                .build();
    }

    public static GetCartResponse empty (Pageable pageable){

        return GetCartResponse.builder()
                .cartResponse(emptyCart())
                .cartItems(PageResponse.of(List.of(), 0, pageable))
                .build();

    }

    public static CartResponse emptyCart (){
        return CartResponse.builder()
                .cartId(null)
                .totalPrice(0)
                .totalQuantity(0)
                .updatedAt(null)
                .build();
    }
}
