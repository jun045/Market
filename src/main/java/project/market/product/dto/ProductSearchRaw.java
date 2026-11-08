package project.market.product.dto;

public record ProductSearchRaw(Long productId,
                               Long categoryId,
                               Long brandId,
                               String categoryName,
                               String brandName,
                               String productName,
                               int price,
                               String thumb) {
}
