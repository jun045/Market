package project.market.PurchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.market.PurchaseOrder.dto.CreateOrderRequest;
import project.market.PurchaseOrder.dto.OrderResponse;

@RestController
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final OrderService orderService;

    //주문 생성
//    @PostMapping
//    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request){
//       OrderResponse response = orderService.createOrder(request);
//    }
}
