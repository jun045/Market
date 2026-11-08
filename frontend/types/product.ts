/** GET /api/v1/products — ProductSearchResponse */
export interface Product {
  id: number;
  categoryName: string;
  brandName: string;
  productName: string;
  price: number;
  thumb: string | null;
}

/** GET /api/v1/products/{productId} — ProductResponse */
export interface ProductDetail {
  id: number;
  name: string;
  description: string;
  thumb: string | null;
  detailImage: string | null;
  listPrice: number;
}

/** GET /api/v1/products/{productId}/variants — UserVariantResponse */
export interface ProductVariant {
  id: number;
  /** JSON 문자열. 예: {"색상":["화이트"],"용량":["256GB"]} */
  options: string;
  salePrice: number;
}
