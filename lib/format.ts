/**
 * 백엔드 옵션 JSON 문자열을 화면용 텍스트로 변환한다.
 * 예: {"색상":["화이트"],"용량":["256GB"]} → "화이트, 256GB"
 */
export function formatOptions(options: string | null | undefined): string {
  if (!options) return "";
  try {
    const parsed: unknown = JSON.parse(options);
    if (parsed && typeof parsed === "object" && !Array.isArray(parsed)) {
      return Object.values(parsed as Record<string, unknown>)
        .flatMap((v) => (Array.isArray(v) ? v : [v]))
        .map(String)
        .join(", ");
    }
  } catch {}
  return options;
}

export function formatPrice(value: number | null | undefined): string {
  return Number(value ?? 0).toLocaleString();
}

/** 2026.04.17 */
export function formatDate(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}.${m}.${day}`;
}

/** 2026. 04. 17. 오후 01:52 */
export function formatDateTime(iso: string | null | undefined): string | null {
  if (!iso) return null;
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return iso;
  return date.toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/** 실제로 표시 가능한 이미지 URL인지 (더미 값 'thumb' 등 제외) */
export function isImageUrl(src: string | null | undefined): src is string {
  return !!src && /^(https?:)?\/\//.test(src);
}
