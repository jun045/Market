import type { OrderStatus } from "@/types/order";

export const ORDER_STATUS_LABEL: Record<OrderStatus, string> = {
  CREATED: "결제 대기",
  PAID: "결제 완료",
  SHIPPED: "배송 중",
  DELIVERED: "배송 완료",
  CANCEL_REQUESTED: "취소 요청",
  CANCELED: "주문 취소",
};
