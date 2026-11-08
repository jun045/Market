declare global {
  interface IamportRequestPayParams {
    pg?: string;
    pay_method?: string;
    merchant_uid: string;
    name: string;
    amount: number;
    buyer_name?: string;
    buyer_email?: string;
    buyer_tel?: string;
    buyer_addr?: string;
    buyer_postcode?: string;
    m_redirect_url?: string;
  }

  interface IamportResponse {
    success: boolean;
    error_code?: string;
    error_msg?: string;
    imp_uid: string | null;
    merchant_uid: string;
    pay_method?: string;
    paid_amount?: number;
    status?: string;
    paid_at?: number;
    pg_provider?: string;
    pg_tid?: string;
    apply_num?: string;
    buyer_name?: string;
    buyer_email?: string;
    buyer_tel?: string;
    buyer_addr?: string;
    buyer_postcode?: string;
  }

  interface Iamport {
    init(merchantCode: string): void;
    request_pay(
      params: IamportRequestPayParams,
      callback: (response: IamportResponse) => void,
    ): void;
  }

  interface Window {
    IMP?: Iamport;
  }
}

export {};
