import type { NextConfig } from "next";

/**
 * 백엔드(Spring Boot) 주소. 브라우저는 같은 오리진의 /api/* 로 요청하고,
 * Next.js 서버가 이를 백엔드로 프록시한다. (백엔드에 CORS 설정이 없어도 동작)
 */
const BACKEND_URL = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${BACKEND_URL}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
