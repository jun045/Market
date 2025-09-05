package project.market.product.dto;

public record CreateProductRequest(String name,
                                   String description,
                                   String thumb,
                                   String detailImage,
                                   int listPrice) {
}
