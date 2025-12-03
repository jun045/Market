package project.market.OrderItem.dto;

//id, 조합이름,개수,스냅샷,가격
public record OrderItemDetailResponse(Long orderItemId,
                                      Long productVariantId,
                                      String productName,
                                      String optionName,
                                      int quantity,
                                      int unitPrice,
                                      int totalPrice  //캐시용 calculate
                                ) {
}
