package project.market.cart.dto;

public record CreateCartItemRequest (Long productId,
                                     Long variantId,
                                     int quantity){
}
