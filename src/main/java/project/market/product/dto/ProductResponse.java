package project.market.product.dto;

public record ProductResponse(Long id,
                              String name,
                              String description,
                              String thumb,
                              String detailImage,
                              int listPrice) {
}
