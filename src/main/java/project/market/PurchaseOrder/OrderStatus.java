package project.market.PurchaseOrder;

public enum OrderStatus {
    CREATED,    // 주문 생성됨 (결제 전)
    PAID,       // 결제 완료
    SHIPPED,    // 배송 시작
    DELIVERED,  // 배송 완료
    CANCEL_REQUESTED, //취소 요청
    CANCELED   // 주문 취소
}
