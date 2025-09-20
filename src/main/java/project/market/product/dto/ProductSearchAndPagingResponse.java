package project.market.product.dto;

import project.market.PageInfo;

import java.util.List;

public record ProductSearchAndPagingResponse(List<ProductSearchResponse> productSearchResponses,
                                             PageInfo pageInfo) {
}
