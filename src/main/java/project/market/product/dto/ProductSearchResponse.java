package project.market.product.dto;

import lombok.Builder;

@Builder
public record ProductSearchResponse(Long id,
                                    String name,
                                    String brandName,
                                    Integer price,
                                    String thumb,
                                    long viewCount) {
}
