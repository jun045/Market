package project.market.product.dto;

public record ProductPageInfo(int pageNumber,
                              int size,
                              long totalElement,
                              int totalPage) {
}
