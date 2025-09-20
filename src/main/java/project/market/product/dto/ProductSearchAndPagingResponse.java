package project.market.product.dto;

import java.util.List;

public record ProductSearchAndPagingResponse(List<ProductSearchResponse> productSearchResponses,
                                             ProductPageInfo pageInfo) {
}
