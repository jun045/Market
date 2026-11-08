import { request } from "@/lib/http";
import type { PageResponse, SpringPage } from "@/types/common";
import type { CreateCartItemRequest, GetCartResponse } from "@/types/cart";
import type { LoginRequest, LoginResponse, SignupRequest } from "@/types/member";
import type { OrderDetail, OrderSummary } from "@/types/order";
import type {
  PaymentConfirmRequest,
  PaymentConfirmResponse,
  PaymentIntentResponse,
  PaymentVerifyRequest,
  PaymentVerifyResponse,
} from "@/types/payment";
import type { Product, ProductDetail, ProductVariant } from "@/types/product";
import type { Review, ReviewRequest } from "@/types/review";

/* ── 회원 ── */

export const memberApi = {
  login: (body: LoginRequest) =>
    request<LoginResponse>("/api/v1/members/login", {
      method: "POST",
      body,
      fallbackMessage: "아이디 또는 비밀번호가 올바르지 않습니다.",
    }),

  signup: (body: SignupRequest) =>
    request<unknown>("/api/v1/members", {
      method: "POST",
      body,
      fallbackMessage:
        "회원가입에 실패했습니다. 이미 사용 중인 아이디 또는 이메일인지 확인해주세요.",
    }),
};

/* ── 상품 ── */

export interface ProductSearchParams {
  /** 1부터 시작하는 페이지 번호 */
  pageNumber: number;
  size?: number;
  categoryId?: number | null;
  keyword?: string | null;
}

export const productApi = {
  /** 상품 목록 — 백엔드는 Spring Pageable(page: 0부터 시작)을 사용 */
  list: ({ pageNumber, size = 20, categoryId, keyword }: ProductSearchParams) =>
    request<PageResponse<Product>>("/api/v1/products", {
      query: { page: pageNumber - 1, size, categoryId, keyword },
      fallbackMessage: "상품 목록을 불러오지 못했습니다.",
    }),

  detail: (productId: number) =>
    request<ProductDetail>(`/api/v1/products/${productId}`, {
      fallbackMessage: "상품 정보를 불러오지 못했습니다.",
    }),

  variants: (productId: number) =>
    request<ProductVariant[]>(`/api/v1/products/${productId}/variants`, {
      fallbackMessage: "옵션 정보를 불러오지 못했습니다.",
    }),

  reviews: (productId: number, pageNumber: number, size = 5) =>
    request<PageResponse<Review>>(`/api/v1/products/${productId}/reviews`, {
      query: { pageNumber, size },
      fallbackMessage: "리뷰 정보를 불러오지 못했습니다.",
    }),
};

/* ── 장바구니 ── */

export const cartApi = {
  get: (pageNumber = 1, size = 100) =>
    request<GetCartResponse>("/api/v1/me/carts", {
      auth: true,
      query: { pageNumber, size },
      fallbackMessage: "장바구니 정보를 불러오지 못했습니다.",
    }),

  addItem: (body: CreateCartItemRequest) =>
    request<unknown>("/api/v1/me/cart/items", {
      method: "POST",
      auth: true,
      body,
      fallbackMessage: "장바구니에 추가하지 못했습니다.",
    }),

  updateQuantity: (cartItemId: number, quantity: number) =>
    request<unknown>(`/api/v1/me/cart/items/${cartItemId}`, {
      method: "PATCH",
      auth: true,
      body: { quantity },
      fallbackMessage: "수량 변경에 실패했습니다.",
    }),

  removeItem: (cartItemId: number) =>
    request<void>(`/api/v1/me/cart/items/${cartItemId}`, {
      method: "DELETE",
      auth: true,
      fallbackMessage: "상품 삭제에 실패했습니다.",
    }),
};

/* ── 주문 ── */

export interface OrderPage {
  content: OrderSummary[];
  /** 1부터 시작 */
  pageNumber: number;
  totalElement: number;
  totalPage: number;
}

type RawSpringPage<T> = Partial<SpringPage<T>> & {
  content: T[];
  /** Spring Data PageSerializationMode.VIA_DTO 형식도 대응 */
  page?: { number: number; size: number; totalElements: number; totalPages: number };
};

export const orderApi = {
  createFromCart: (cartItemIds: number[]) =>
    request<OrderDetail>("/api/v1/orders/cart", {
      method: "POST",
      auth: true,
      body: { cartItemIds },
      fallbackMessage: "주문 생성에 실패했습니다.",
    }),

  /** 내 주문 목록 — 백엔드는 Spring Page(page: 0부터 시작)를 그대로 반환 */
  list: async (pageNumber: number, size = 10): Promise<OrderPage> => {
    const data = await request<RawSpringPage<OrderSummary>>("/api/v1/orders", {
      auth: true,
      query: { page: pageNumber - 1, size },
      fallbackMessage: "주문 내역을 불러오지 못했습니다.",
    });
    const meta = data.page ?? {
      number: data.number ?? 0,
      totalElements: data.totalElements ?? data.content.length,
      totalPages: data.totalPages ?? 1,
    };
    return {
      content: data.content,
      pageNumber: meta.number + 1,
      totalElement: meta.totalElements,
      totalPage: meta.totalPages,
    };
  },

  detail: (orderId: number) =>
    request<OrderDetail>(`/api/v1/orders/${orderId}`, {
      auth: true,
      fallbackMessage: "주문 정보를 불러오지 못했습니다.",
    }),

  requestCancel: (orderId: number) =>
    request<void>(`/api/v1/orders/${orderId}/cancel-request`, {
      method: "PUT",
      auth: true,
      fallbackMessage: "주문 취소에 실패했습니다.",
    }),
};

/* ── 결제 ── */

export const paymentApi = {
  prepare: (orderId: number) =>
    request<PaymentIntentResponse>(`/api/v1/payments/${orderId}/validation`, {
      method: "POST",
      auth: true,
      fallbackMessage: "결제 준비에 실패했습니다.",
    }),

  verify: (body: PaymentVerifyRequest) =>
    request<PaymentVerifyResponse>("/api/v1/payments/verify", {
      method: "POST",
      auth: true,
      body,
      fallbackMessage: "결제 검증에 실패했습니다.",
    }),

  confirm: (body: PaymentConfirmRequest) =>
    request<PaymentConfirmResponse>("/api/v1/payments/confirm", {
      method: "POST",
      auth: true,
      body,
      fallbackMessage: "결제 확인 처리에 실패했습니다.",
    }),
};

/* ── 리뷰 ── */

export const reviewApi = {
  create: (orderItemId: number, body: ReviewRequest) =>
    request<Review>(`/api/v1/order_item/${orderItemId}/reviews`, {
      method: "POST",
      auth: true,
      body,
      fallbackMessage: "리뷰 등록에 실패했습니다.",
    }),
};
