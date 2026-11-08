'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import SectionLabel from '@/components/common/SectionLabel';
import ProductCard from '@/components/common/ProductCard';
import { productApi } from '@/lib/api';
import type { Product } from '@/types/product';
import styles from './BestSellerSection.module.css';

const DISPLAY_COUNT = 4;

export default function BestSellerSection() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    async function fetchBestSellers() {
      try {
        const data = await productApi.list({ pageNumber: 1, size: DISPLAY_COUNT });
        if (!isMounted) return;
        setProducts(data.content.slice(0, DISPLAY_COUNT));
      } catch (error) {
        console.error(error);
        if (!isMounted) return;
        setProducts([]);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    }

    fetchBestSellers();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <SectionLabel label="BEST SELLER" />
        <div className={styles.header}>
          <h2 className={styles.title}>
            이번 주 <span className={styles.accent}>베스트셀러</span>
          </h2>
          <Link href="/products" className={styles.viewAll}>
            전체 보기 →
          </Link>
        </div>

        {isLoading ? (
          <div className={styles.empty}>베스트 상품을 불러오는 중...</div>
        ) : products.length > 0 ? (
          <div className={styles.grid}>
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        ) : (
          <div className={styles.empty}>표시할 상품이 없습니다.</div>
        )}
      </div>
    </section>
  );
}
