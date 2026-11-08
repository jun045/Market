import type { PageResponse } from "@/types/common";

/** CartItemResponse */
export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  thumb: string | null;
  variantId: number;
  /** JSON 문자열 옵션 */
  options: string;
  quantity: number;
  /** 옵션 단가 (할인/추가금 반영) */
  salePrice: number;
  /** 단가 × 수량 */
  itemPrice: number;
  createdAt: string;
  updatedAt: string;
}

/** CartResponse */
export interface CartSummary {
  cartId: number | null;
  totalPrice: number;
  totalQuantity: number;
  updatedAt: string | null;
}

/** GET /api/v1/me/carts — GetCartResponse */
export interface GetCartResponse {
  cartResponse: CartSummary;
  cartItems: PageResponse<CartItem>;
}

/** POST /api/v1/me/cart/items — CreateCartItemRequest */
export interface CreateCartItemRequest {
  productId: number;
  variantId: number;
  quantity: number;
}
