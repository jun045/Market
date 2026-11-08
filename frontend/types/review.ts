/** ReviewResponse */
export interface Review {
  reviewId: number;
  nickname: string;
  productName: string;
  /** JSON 문자열 옵션 */
  option: string | null;
  rating: number;
  content: string;
  createdAt: string;
  updatedAt: string;
}

/** POST /api/v1/order_item/{orderItemId}/reviews — ReviewRequest */
export interface ReviewRequest {
  rating: number;
  content: string;
}
