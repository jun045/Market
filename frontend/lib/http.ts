import { clearToken, getToken } from "@/lib/auth";
import type { ApiErrorBody } from "@/types/common";

export class ApiError extends Error {
  readonly status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

/** 토큰이 없거나 만료되어 로그인이 필요한 경우 */
export class AuthRequiredError extends ApiError {
  constructor() {
    super("로그인이 필요합니다.", 401);
    this.name = "AuthRequiredError";
  }
}

type Query = Record<string, string | number | null | undefined>;

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  query?: Query;
  body?: unknown;
  /** true면 Authorization 헤더를 붙이고, 401/403 응답 시 토큰을 지운다 */
  auth?: boolean;
  /** 백엔드가 message를 주지 않을 때 사용할 에러 메시지 */
  fallbackMessage?: string;
}

function buildUrl(path: string, query?: Query) {
  if (!query) return path;
  const params = new URLSearchParams();
  for (const [key, value] of Object.entries(query)) {
    if (value !== null && value !== undefined && value !== "") {
      params.set(key, String(value));
    }
  }
  const qs = params.toString();
  return qs ? `${path}?${qs}` : path;
}

/**
 * 백엔드 API 호출.
 * 경로는 "/api/v1/..." 형태이며 next.config.ts의 rewrites로 백엔드(BACKEND_URL)에 프록시된다.
 */
export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const {
    method = "GET",
    query,
    body,
    auth = false,
    fallbackMessage = "요청 처리에 실패했습니다.",
  } = options;

  const headers: Record<string, string> = {};
  if (body !== undefined) headers["Content-Type"] = "application/json";

  if (auth) {
    const token = getToken();
    if (!token) throw new AuthRequiredError();
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(buildUrl(path, query), {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
    cache: "no-store",
  });

  // 인증이 필요한 요청의 401/403 → 토큰 만료 또는 무효 (Spring Security 기본 응답은 403)
  if (auth && (res.status === 401 || res.status === 403)) {
    clearToken();
    throw new AuthRequiredError();
  }

  const text = await res.text();

  if (!res.ok) {
    let message = fallbackMessage;
    try {
      const data = JSON.parse(text) as ApiErrorBody;
      if (typeof data.message === "string" && data.message.trim()) {
        message = data.message;
      }
    } catch {}
    throw new ApiError(message, res.status);
  }

  if (!text) return undefined as T;
  return JSON.parse(text) as T;
}

export function errorMessage(error: unknown, fallback: string): string {
  return error instanceof Error && error.message ? error.message : fallback;
}
