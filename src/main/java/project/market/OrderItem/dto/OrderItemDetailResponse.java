package project.market.OrderItem.dto;

//id, 조합id,상품명,옵션조합명,개수,단가,가격
public record OrderItemDetailResponse(Long orderItemId,
                                      Long productVariantId,
                                      String productName,
                                      String optionName,
                                      int quantity,
                                      int unitPrice,
                                      int totalPrice
                                ) {
}
