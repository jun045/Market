export type PayStatus = "READY" | "PAID" | "FAILED";

/** POST /api/v1/payments/{purchaseOrderId}/validation */
export interface PaymentIntentResponse {
  merchantUid: string;
  amount: number;
  payStatus: PayStatus;
  memberId: number;
  purchaseOrderId: number;
  buyerName: string | null;
  buyerEmail: string | null;
}

/** POST /api/v1/payments/verify */
export interface PaymentVerifyRequest {
  impUid: string;
  merchantUid: string;
  amount: number;
}

export interface PaymentVerifyResponse {
  success: boolean;
  message: string;
  merchantUid: string;
  amount: number;
  payStatus: PayStatus;
  paidAt: string | null;
  buyerName: string | null;
  buyerEmail: string | null;
  paidMethod: string | null;
  pgProvider: string | null;
}

/** POST /api/v1/payments/confirm */
export interface PaymentConfirmRequest {
  impUid: string;
  merchantUid: string;
}

export interface PaymentConfirmResponse {
  impUid: string;
  merchantUid: string;
  payMethod: string;
  pgProvider: string;
  finalPaymentAmount: number;
  amount: number;
  usedPoint: number;
  earnedPoint: number;
  remainPoint: number;
  payStatus: PayStatus;
  buyerEmail: string;
  buyerName: string;
  paidAt: string;
  message: string[];
}
