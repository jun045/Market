"use client";

import { ReactNode, useState } from "react";
import { isImageUrl } from "@/lib/format";

interface SafeImageProps {
  src: string | null | undefined;
  alt: string;
  className?: string;
  /** 이미지가 없거나 로드에 실패했을 때 보여줄 요소 */
  fallback: ReactNode;
}

/** URL이 유효하지 않거나 로드에 실패하면 fallback을 보여주는 이미지 */
export default function SafeImage({ src, alt, className, fallback }: SafeImageProps) {
  const [failedSrc, setFailedSrc] = useState<string | null>(null);

  if (!isImageUrl(src) || failedSrc === src) return <>{fallback}</>;

  return (
    // eslint-disable-next-line @next/next/no-img-element
    <img src={src} alt={alt} className={className} onError={() => setFailedSrc(src)} />
  );
}
