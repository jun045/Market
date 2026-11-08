export const TOKEN_KEY = "accessToken";

/** 같은 탭 안에서 로그인 상태가 바뀌었음을 알리는 이벤트 */
export const AUTH_CHANGE_EVENT = "auth-change";

/** 장바구니 내용이 바뀌었음을 알리는 이벤트 (헤더 장바구니 수량 갱신용) */
export const CART_CHANGE_EVENT = "cart-change";

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token);
  window.dispatchEvent(new Event(AUTH_CHANGE_EVENT));
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
  window.dispatchEvent(new Event(AUTH_CHANGE_EVENT));
}

export function notifyCartChange() {
  window.dispatchEvent(new Event(CART_CHANGE_EVENT));
}
