/** 백엔드 PageResponse<T> (pageNumber는 1부터 시작) */
export interface PageInfo {
  pageNumber: number;
  size: number;
  totalElement: number;
  totalPage: number;
}

export interface PageResponse<T> {
  content: T[];
  pageInfo: PageInfo;
}

/** 백엔드에서 Spring Page<T>를 그대로 직렬화하는 응답 (number는 0부터 시작) */
export interface SpringPage<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/** 백엔드 ApiResponse (에러 응답 바디) */
export interface ApiErrorBody {
  message?: string;
  body?: unknown;
}
