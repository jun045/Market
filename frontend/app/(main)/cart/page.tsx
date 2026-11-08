"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import Button from "@/components/common/Button";
import SafeImage from "@/components/common/SafeImage";
import { cartApi, orderApi } from "@/lib/api";
import { getToken, notifyCartChange } from "@/lib/auth";
import { formatOptions, formatPrice } from "@/lib/format";
import { AuthRequiredError, errorMessage } from "@/lib/http";
import type { CartItem, GetCartResponse } from "@/types/cart";
import styles from "./page.module.css";

export default function CartPage() {
  const router = useRouter();

  const [cart, setCart] = useState<GetCartResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessageText, setErrorMessageText] = useState<string | null>(null);
  const [pendingItemId, setPendingItemId] = useState<number | null>(null);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [isOrdering, setIsOrdering] = useState(false);

  const handleAuthError = useCallback(
    (error: unknown) => {
      if (error instanceof AuthRequiredError) {
        router.replace("/login");
        return true;
      }
      return false;
    },
    [router],
  );

  /** 장바구니 조회. 최초 로딩 상태는 isLoading 초기값(true)이 담당한다 */
  const fetchCart = useCallback(
    () =>
      cartApi
        .get()
        .then((data) => {
          setErrorMessageText(null);
          setCart(data);

          const currentIds = data.cartItems.content.map((i) => i.id);
          setSelectedIds(
            (prev) => new Set(currentIds.filter((id) => prev.has(id))),
          );
        })
        .catch((error: unknown) => {
          console.error(error);
          if (handleAuthError(error)) return;
          setErrorMessageText("장바구니 정보를 불러오지 못했습니다.");
        })
        .finally(() => setIsLoading(false)),
    [handleAuthError],
  );

  useEffect(() => {
    if (!getToken()) {
      router.replace("/login");
      return;
    }
    fetchCart();
  }, [fetchCart, router]);

  async function handleQuantityChange(item: CartItem, nextQuantity: number) {
    if (nextQuantity < 1) return;
    if (nextQuantity === item.quantity) return;

    try {
      setPendingItemId(item.id);
      await cartApi.updateQuantity(item.id, nextQuantity);
      await fetchCart();
      notifyCartChange();
    } catch (error) {
      console.error(error);
      if (handleAuthError(error)) return;
      alert(errorMessage(error, "수량 변경에 실패했습니다."));
    } finally {
      setPendingItemId(null);
    }
  }

  async function handleOrder(itemIds: number[]) {
    if (itemIds.length === 0) return;

    try {
      setIsOrdering(true);
      const order = await orderApi.createFromCart(itemIds);
      notifyCartChange();
      alert("주문이 완료되었습니다.");
      router.push(`/orders/${order.id}`);
    } catch (error) {
      console.error(error);
      if (handleAuthError(error)) return;
      alert(errorMessage(error, "주문 생성에 실패했습니다."));
    } finally {
      setIsOrdering(false);
    }
  }

  async function handleDelete(item: CartItem) {
    if (!confirm(`"${item.productName}" 상품을 장바구니에서 삭제하시겠습니까?`)) {
      return;
    }

    try {
      setPendingItemId(item.id);
      await cartApi.removeItem(item.id);
      await fetchCart();
      notifyCartChange();
    } catch (error) {
      console.error(error);
      if (handleAuthError(error)) return;
      alert(errorMessage(error, "상품 삭제에 실패했습니다."));
    } finally {
      setPendingItemId(null);
    }
  }

  if (isLoading) {
    return (
      <div className={styles.state}>
        <span>장바구니를 불러오는 중...</span>
      </div>
    );
  }

  if (errorMessageText || !cart) {
    return (
      <div className={styles.state}>
        <span>{errorMessageText ?? "장바구니를 불러올 수 없습니다."}</span>
      </div>
    );
  }

  const summary = cart.cartResponse;
  const items = cart.cartItems.content;
  const isEmpty = summary.cartId === null || items.length === 0;

  const toggleSelected = (id: number) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const selectedItems = items.filter((i) => selectedIds.has(i.id));
  const selectedQuantity = selectedItems.reduce(
    (sum, i) => sum + i.quantity,
    0,
  );
  const selectedAmount = selectedItems.reduce(
    (sum, i) => sum + Number(i.itemPrice),
    0,
  );

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <header className={styles.pageHeader}>
          <h1 className={styles.title}>장바구니</h1>
          <p className={styles.subtitle}>
            총 {summary.totalQuantity}개의 상품이 담겨 있습니다.
          </p>
        </header>

        {isEmpty ? (
          <div className={styles.empty}>
            <p className={styles.emptyText}>장바구니에 담긴 상품이 없습니다.</p>
            <Link href="/" className={styles.emptyLink}>
              쇼핑하러 가기
            </Link>
          </div>
        ) : (
          <div className={styles.layout}>
            <ul className={styles.itemList}>
              {items.map((item) => {
                const isPending = pendingItemId === item.id;
                return (
                  <li key={item.id} className={styles.item}>
                    <input
                      type="checkbox"
                      className={styles.checkbox}
                      checked={selectedIds.has(item.id)}
                      onChange={() => toggleSelected(item.id)}
                      aria-label={`${item.productName} 선택`}
                    />
                    <Link
                      href={`/products/${item.productId}`}
                      className={styles.thumbLink}
                    >
                      <SafeImage
                        src={item.thumb}
                        alt={item.productName}
                        className={styles.thumb}
                        fallback={<span className={styles.thumb} />}
                      />
                    </Link>

                    <div className={styles.itemInfo}>
                      <Link
                        href={`/products/${item.productId}`}
                        className={styles.itemName}
                      >
                        {item.productName}
                      </Link>
                      <p className={styles.itemOption}>
                        {formatOptions(item.options)}
                      </p>
                      <p className={styles.itemUnitPrice}>
                        {formatPrice(item.salePrice)}원
                      </p>
                    </div>

                    <div className={styles.quantityControl}>
                      <button
                        type="button"
                        className={styles.qtyBtn}
                        onClick={() =>
                          handleQuantityChange(item, item.quantity - 1)
                        }
                        disabled={isPending || item.quantity <= 1}
                      >
                        −
                      </button>
                      <span className={styles.qtyValue}>{item.quantity}</span>
                      <button
                        type="button"
                        className={styles.qtyBtn}
                        onClick={() =>
                          handleQuantityChange(item, item.quantity + 1)
                        }
                        disabled={isPending}
                      >
                        +
                      </button>
                    </div>

                    <div className={styles.itemTotal}>
                      {formatPrice(item.itemPrice)}원
                    </div>

                    <button
                      type="button"
                      className={styles.deleteBtn}
                      onClick={() => handleDelete(item)}
                      disabled={isPending}
                      aria-label="상품 삭제"
                    >
                      삭제
                    </button>
                  </li>
                );
              })}
            </ul>

            <aside className={styles.summary}>
              <h2 className={styles.summaryTitle}>주문 요약</h2>

              <div className={styles.summaryRow}>
                <span>상품 수</span>
                <span>{summary.totalQuantity}개</span>
              </div>
              <div className={styles.summaryRow}>
                <span>상품 금액</span>
                <span>{formatPrice(summary.totalPrice)}원</span>
              </div>

              <div className={styles.summaryDivider} />

              <div className={styles.selectedBox}>
                <div className={styles.selectedHeader}>선택 상품</div>
                <div className={styles.summaryRow}>
                  <span>선택 상품 수</span>
                  <span>{selectedQuantity}개</span>
                </div>
                <div className={styles.summaryRow}>
                  <span>선택 상품 금액</span>
                  <span>{formatPrice(selectedAmount)}원</span>
                </div>
                <div className={styles.summaryTotal}>
                  <span>총 결제 금액</span>
                  <span className={styles.summaryTotalPrice}>
                    {formatPrice(selectedAmount)}원
                  </span>
                </div>
              </div>

              <Button
                variant="primary"
                className={styles.orderButton}
                onClick={() => handleOrder(selectedItems.map((i) => i.id))}
                disabled={selectedItems.length === 0 || isOrdering}
              >
                {isOrdering
                  ? "주문 처리 중..."
                  : `주문하기 (${selectedItems.length})`}
              </Button>
            </aside>
          </div>
        )}
      </div>
    </section>
  );
}
