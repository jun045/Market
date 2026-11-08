"use client";

import { useCallback, useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import ImageGallery from "@/components/product/ImageGallery";
import VariantSelector from "@/components/product/VariantSelector";
import Button from "@/components/common/Button";
import ReviewSection from "@/components/product/ReviewSection";
import { cartApi, productApi } from "@/lib/api";
import { getToken, notifyCartChange } from "@/lib/auth";
import { formatPrice } from "@/lib/format";
import { AuthRequiredError, errorMessage } from "@/lib/http";
import type { PageResponse } from "@/types/common";
import type { ProductDetail, ProductVariant } from "@/types/product";
import type { Review } from "@/types/review";
import styles from "./page.module.css";

const MAX_QUANTITY = 99;

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();
  const productId = Number(params.id);

  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [variants, setVariants] = useState<ProductVariant[]>([]);
  const [selectedVariant, setSelectedVariant] = useState<ProductVariant | null>(
    null,
  );
  const [quantity, setQuantity] = useState(1);
  const [reviewData, setReviewData] = useState<PageResponse<Review> | null>(
    null,
  );

  const [isLoading, setIsLoading] = useState(true);
  const [errorMessageText, setErrorMessageText] = useState<string | null>(null);
  const [isAddingToCart, setIsAddingToCart] = useState(false);

  const fetchReviews = useCallback(
    async (page: number) => {
      try {
        const data = await productApi.reviews(productId, page);
        setReviewData(data);
      } catch (error) {
        console.error(error);
        setReviewData(null);
      }
    },
    [productId],
  );

  const isValidId = Number.isInteger(productId) && productId > 0;

  useEffect(() => {
    if (!isValidId) return;

    let isMounted = true;

    async function fetchProductDetail() {
      try {
        setIsLoading(true);
        setErrorMessageText(null);

        const [detail, variantList] = await Promise.all([
          productApi.detail(productId),
          productApi.variants(productId).catch((error) => {
            console.error(error);
            return [] as ProductVariant[];
          }),
        ]);

        if (!isMounted) return;

        setProduct(detail);
        setVariants(variantList);
        setSelectedVariant(null);
        setQuantity(1);
      } catch (error) {
        console.error(error);
        if (!isMounted) return;
        setErrorMessageText("상품 정보를 불러오지 못했습니다.");
      } finally {
        if (isMounted) setIsLoading(false);
      }
    }

    fetchProductDetail();
    productApi
      .reviews(productId, 1)
      .then((data) => {
        if (isMounted) setReviewData(data);
      })
      .catch((error) => console.error(error));

    return () => {
      isMounted = false;
    };
  }, [isValidId, productId]);

  if (!isValidId) {
    return (
      <div className={styles.loading}>
        <span>잘못된 상품 번호입니다.</span>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span>상품 정보를 불러오는 중...</span>
      </div>
    );
  }

  if (errorMessageText || !product) {
    return (
      <div className={styles.loading}>
        <span>{errorMessageText ?? "상품 정보를 찾을 수 없습니다."}</span>
      </div>
    );
  }

  const displayPrice = selectedVariant
    ? selectedVariant.salePrice
    : product.listPrice;

  // 구매 가능한 옵션(삭제되지 않은 옵션)이 하나도 없으면 품절로 표시
  const isSoldOut = variants.length === 0;

  async function handleAddToCart() {
    if (!product) return;

    if (isSoldOut) {
      alert("품절된 상품입니다.");
      return;
    }

    if (!selectedVariant) {
      alert("옵션을 선택해주세요.");
      return;
    }

    if (!getToken()) {
      router.push("/login");
      return;
    }

    try {
      setIsAddingToCart(true);
      await cartApi.addItem({
        productId: product.id,
        variantId: selectedVariant.id,
        quantity,
      });
      notifyCartChange();
      alert("장바구니에 넣었습니다.");
    } catch (error) {
      console.error(error);
      if (error instanceof AuthRequiredError) {
        router.push("/login");
        return;
      }
      alert(errorMessage(error, "장바구니에 추가하지 못했습니다."));
    } finally {
      setIsAddingToCart(false);
    }
  }

  function decreaseQty() {
    setQuantity((q) => Math.max(1, q - 1));
  }

  function increaseQty() {
    setQuantity((q) => Math.min(MAX_QUANTITY, q + 1));
  }

  const priceDiff = selectedVariant
    ? selectedVariant.salePrice - product.listPrice
    : 0;

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        {/* 브레드크럼 */}
        <nav className={styles.breadcrumb}>
          <Link href="/">홈</Link>
          <span className={styles.sep}>/</span>
          <Link href="/products">전체 상품</Link>
          <span className={styles.sep}>/</span>
          <span>{product.name}</span>
        </nav>

        <div className={styles.layout}>
          {/* 좌: 이미지 */}
          <div className={styles.imageCol}>
            <ImageGallery
              thumb={product.thumb}
              detailImage={product.detailImage}
              productName={product.name}
            />
          </div>

          {/* 우: 상품 정보 */}
          <div className={styles.infoCol}>
            <div className={styles.brandRow}>
              {isSoldOut ? (
                <span className={styles.badgeSoldOut}>품절</span>
              ) : (
                <span className={styles.badge}>판매중</span>
              )}
            </div>

            <h1 className={styles.name}>{product.name}</h1>

            <div className={styles.priceArea}>
              <span className={styles.price}>
                {formatPrice(displayPrice)}원
              </span>
              {priceDiff !== 0 && (
                <span className={styles.basePrice}>
                  정가 {formatPrice(product.listPrice)}원
                </span>
              )}
            </div>

            <p className={styles.description}>{product.description}</p>

            <div className={styles.divider} />

            {/* 옵션 선택 */}
            <VariantSelector
              variants={variants}
              listPrice={product.listPrice}
              selectedId={selectedVariant?.id ?? null}
              onSelect={(v) => {
                setSelectedVariant(v);
                setQuantity(1);
              }}
              disabled={isSoldOut}
            />

            {/* 수량 선택 */}
            {selectedVariant && (
              <div className={styles.quantityArea}>
                <span className={styles.quantityLabel}>수량</span>
                <div className={styles.quantityControl}>
                  <button className={styles.qtyBtn} onClick={decreaseQty}>
                    −
                  </button>
                  <span className={styles.qtyValue}>{quantity}</span>
                  <button className={styles.qtyBtn} onClick={increaseQty}>
                    +
                  </button>
                </div>
              </div>
            )}

            <div className={styles.divider} />

            {/* 총 금액 + 장바구니 */}
            <div className={styles.totalRow}>
              <span className={styles.totalLabel}>총 상품 금액</span>
              <span className={styles.totalPrice}>
                {formatPrice(displayPrice * quantity)}원
              </span>
            </div>

            <Button
              variant="primary"
              className={styles.cartButton}
              onClick={handleAddToCart}
              disabled={isAddingToCart || isSoldOut}
            >
              {isSoldOut
                ? "품절"
                : isAddingToCart
                  ? "담는 중..."
                  : "장바구니 담기"}
            </Button>
          </div>
        </div>

        {/* 리뷰 섹션 */}
        {reviewData && (
          <ReviewSection data={reviewData} onPageChange={fetchReviews} />
        )}
      </div>
    </section>
  );
}
