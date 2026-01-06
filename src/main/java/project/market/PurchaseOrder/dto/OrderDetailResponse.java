package project.market.PurchaseOrder.dto;

import project.market.OrderItem.dto.OrderItemDetailResponse;
import project.market.PurchaseOrder.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

//id값, 상태, 결제날짜, 총가격, 사용포인트, 적립포인트, 결제예정금액, 조합
public record OrderDetailResponse(Long id,
                                  String merchantUid,
                                  OrderStatus orderStatus,
                                  LocalDateTime orderDate,
                                  int orderTotalPrice,
                                  int usedPoint,
                                  int earnPoint,
                                  int payAmount,
                                  List<OrderItemDetailResponse> orderItems
                            ) {
}
