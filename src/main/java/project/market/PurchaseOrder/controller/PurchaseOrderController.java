package project.market.PurchaseOrder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.market.PurchaseOrder.OrderStatus;
import project.market.PurchaseOrder.dto.*;
import project.market.PurchaseOrder.service.CartOrderService;
import project.market.PurchaseOrder.service.OrderService;
import project.market.member.Entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PurchaseOrderController {
    private final OrderService orderService;
    private final CartOrderService cartOrderService;

    /**
     * 사용자
     **/
    //주문 생성
    @PostMapping("/orders")
    public OrderDetailResponse createOrder(@AuthenticationPrincipal(expression = "member") Member member,
                                           @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(member, request);
    }

    //전체 조회 - 사용자
    @GetMapping("/orders")
    public Page<OrderListResponse> userFindAllOrder(@AuthenticationPrincipal(expression = "member") Member member,
                                                    @RequestParam(required = false) String merchantUid,
                                                    @RequestParam(required = false) LocalDateTime startDate,
                                                    @RequestParam(required = false) LocalDateTime endDate,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        UserOrderSearchDto dto = new UserOrderSearchDto(merchantUid, startDate, endDate);
        return orderService.userFindAllOrder(member, dto, pageable);
    }

    //상세 조회 - 사용자
    @GetMapping("/orders/{orderId}")
    public OrderDetailResponse userFindOrder(@AuthenticationPrincipal(expression = "member") Member member,
                                             @PathVariable Long orderId) {
        return orderService.userFindOrder(member, orderId);
    }

    //주문 취소 요청 - 사용자
    @PutMapping("/orders/{orderId}/cancel-request")
    public ResponseEntity<Void> requestCancel(@AuthenticationPrincipal(expression = "member") Member member,
                                              @PathVariable Long orderId) {
        orderService.userRequestCancelOrder(member, orderId);
        return ResponseEntity.ok().build();
    }

    //장바구니 주문
    @PostMapping("/orders/cart")
    public OrderDetailResponse orderCartItems(@AuthenticationPrincipal(expression = "member") Member member,
                                              @RequestBody CreateCartOrderRequest request) {
        return cartOrderService.orderCartItems(member, request);
    }

    /**
     * 관리자
     **/
    //전체 조회 + 검색 기능(주문번호, 이메일,기간,주문상태) - 관리자
    @GetMapping("/admin/orders")
    public Page<OrderListResponse> adminFindAllOrder(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam(required = false) String merchantUid,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String memberEmail,
            @PageableDefault(size = 20) Pageable pageable) {
        OrderSearchDto dto = new OrderSearchDto(merchantUid, orderStatus, startDate, endDate, memberEmail);
        return orderService.adminSearchOrders(member, dto, pageable);
    }

    //상세 조회 - 관리자
    @GetMapping("/admin/orders/{orderId}")
    public OrderDetailResponse adminFindOrder(@AuthenticationPrincipal(expression = "member") Member member,
                                              @PathVariable Long orderId) {
        return orderService.adminFindOrder(member, orderId);
    }

    //주문 상태 수정 - 관리자
    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<Void> adminUpdateStatus(@AuthenticationPrincipal(expression = "member") Member member,
                                                  @PathVariable Long orderId,
                                                  @RequestParam OrderStatus newStatus) {
        orderService.adminUpdateOrderStatus(member, orderId, newStatus);
        return ResponseEntity.ok().build();
    }

    //주문 취소 승인 - 관리자
    @PutMapping("/admin/orders/{orderId}/approve-cancel")
    public ResponseEntity<Void> approveCancelRequest(@AuthenticationPrincipal(expression = "member") Member member,
                                                     @PathVariable Long orderId) {
        orderService.approveCancelRequest(member, orderId);
        return ResponseEntity.ok().build();
    }

    //주문 삭제 - 관리자
    @DeleteMapping("/admin/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@AuthenticationPrincipal(expression = "member") Member member,
                                            @PathVariable Long orderId) {
        orderService.deleteOrder(member, orderId);
        return ResponseEntity.ok().build();
    }

}
