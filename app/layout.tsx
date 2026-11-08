import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'SHOP. | 믿을 수 있는 쇼핑 경험',
  description: '엄선된 브랜드와 검증된 상품만 모은 프리미엄 쇼핑몰',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
