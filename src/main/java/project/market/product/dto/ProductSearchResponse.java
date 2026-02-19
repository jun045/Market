package project.market.product.dto;

public record ProductSearchResponse(Long id,
                                    String categoryName,
                                    String brandName,
                                    String productName,
                                    int price) {
}
