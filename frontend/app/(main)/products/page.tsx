"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import SectionLabel from "@/components/common/SectionLabel";
import ProductCard from "@/components/common/ProductCard";
import Pagination from "@/components/common/Pagination";
import { findChildCategoryName } from "@/constants/categories";
import { productApi } from "@/lib/api";
import type { PageInfo } from "@/types/common";
import type { Product } from "@/types/product";
import styles from "./page.module.css";

const PAGE_SIZE = 20;
const BEST_MODE_MAX_PAGES = 5;

type Mode = "best" | "category" | "keyword";

export default function ProductListPage() {
  return (
    <Suspense
      fallback={
        <div className={styles.section}>
          <div className={styles.container}>
            <div className={styles.empty}>상품 목록을 불러오는 중...</div>
          </div>
        </div>
      }
    >
      <ProductListContent />
    </Suspense>
  );
}

function ProductListContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const categoryIdRaw = searchParams.get("categoryId");
  const keywordRaw = searchParams.get("keyword");
  const pageNumberRaw = Number(searchParams.get("pageNumber"));

  const categoryId = categoryIdRaw ? Number(categoryIdRaw) || null : null;
  const keyword = keywordRaw?.trim() || null;
  const pageNumber =
    Number.isFinite(pageNumberRaw) && pageNumberRaw >= 1
      ? Math.floor(pageNumberRaw)
      : 1;

  const mode: Mode = categoryId ? "category" : keyword ? "keyword" : "best";

  const [products, setProducts] = useState<Product[]>([]);
  const [pageInfo, setPageInfo] = useState<PageInfo>({
    pageNumber: 1,
    size: PAGE_SIZE,
    totalElement: 0,
    totalPage: 1,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function fetchProducts() {
      try {
        setIsLoading(true);
        setErrorMessage(null);

        const data = await productApi.list({
          pageNumber,
          size: PAGE_SIZE,
          categoryId,
          keyword,
        });
        if (!isMounted) return;
        setProducts(data.content);
        setPageInfo(data.pageInfo);
      } catch (error) {
        console.error(error);
        if (!isMounted) return;
        setErrorMessage("상품 목록을 불러오지 못했습니다.");
        setProducts([]);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    }

    fetchProducts();

    return () => {
      isMounted = false;
    };
  }, [categoryId, keyword, pageNumber]);

  function handlePageChange(page: number) {
    const params = new URLSearchParams();
    if (categoryId) params.set("categoryId", String(categoryId));
    if (keyword) params.set("keyword", keyword);
    params.set("pageNumber", String(page));
    router.push(`/products?${params.toString()}`);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const title = getTitle(mode, categoryId, keyword);
  const displayTotalPage =
    mode === "best"
      ? Math.min(pageInfo.totalPage, BEST_MODE_MAX_PAGES)
      : pageInfo.totalPage;

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <SectionLabel label={getLabel(mode)} />
        <div className={styles.header}>
          <h1 className={styles.title}>{title}</h1>
          <span className={styles.count}>
            {mode === "best"
              ? `베스트 ${Math.min(
                  pageInfo.totalElement,
                  BEST_MODE_MAX_PAGES * pageInfo.size,
                ).toLocaleString()}개`
              : `총 ${pageInfo.totalElement.toLocaleString()}개`}
          </span>
        </div>

        {isLoading ? (
          <div className={styles.empty}>상품 목록을 불러오는 중...</div>
        ) : errorMessage ? (
          <div className={styles.empty}>{errorMessage}</div>
        ) : products.length > 0 ? (
          <div className={styles.grid}>
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        ) : (
          <div className={styles.empty}>
            {mode === "keyword"
              ? `'${keyword}' 검색 결과가 없습니다.`
              : "등록된 상품이 없습니다."}
          </div>
        )}

        <Pagination
          currentPage={pageNumber}
          totalPage={displayTotalPage}
          onPageChange={handlePageChange}
        />
      </div>
    </section>
  );
}

function getLabel(mode: Mode): string {
  switch (mode) {
    case "category":
      return "CATEGORY";
    case "keyword":
      return "SEARCH RESULTS";
    default:
      return "BEST";
  }
}

function getTitle(
  mode: Mode,
  categoryId: number | null,
  keyword: string | null,
) {
  if (mode === "category" && categoryId !== null) {
    const name = findChildCategoryName(categoryId);
    return (
      <span className={styles.accent}>{name ?? `카테고리 #${categoryId}`}</span>
    );
  }

  if (mode === "keyword" && keyword) {
    return (
      <>
        <span className={styles.accent}>&lsquo;{keyword}&rsquo;</span> 검색 결과
      </>
    );
  }

  return (
    <>
      이번 주 <span className={styles.accent}>베스트</span>
    </>
  );
}
