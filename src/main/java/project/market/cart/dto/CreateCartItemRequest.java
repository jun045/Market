package project.market.cart.dto;

public record CreateCartItemRequest(Long productId,
                                    Long optionVariantId,
                                    Integer quantity) {
}
