export type OrderStatus =
  | "CREATED"
  | "PAID"
  | "SHIPPED"
  | "DELIVERED"
  | "CANCEL_REQUESTED"
  | "CANCELED";

/** OrderItemDetailResponse */
export interface OrderItem {
  orderItemId: number;
  productVariantId: number;
  productName: string;
  /** JSON 문자열 옵션 */
  optionName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

/** GET /api/v1/orders/{orderId} — OrderDetailResponse */
export interface OrderDetail {
  id: number;
  merchantUid: string;
  orderStatus: OrderStatus;
  orderDate: string;
  orderTotalPrice: number;
  usedPoint: number;
  earnPoint: number;
  payAmount: number;
  orderItems: OrderItem[];
}

/** GET /api/v1/orders — OrderListResponse (Spring Page) */
export interface OrderSummary {
  id: number;
  orderStatus: OrderStatus;
  orderDate: string;
  orderTotalPrice: number;
  payAmount: number;
}

/** POST /api/v1/orders/cart — CreateCartOrderRequest */
export interface CreateCartOrderRequest {
  cartItemIds: number[];
}
