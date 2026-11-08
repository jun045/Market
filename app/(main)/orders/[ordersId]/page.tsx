"use client";

import { useParams, useRouter } from "next/navigation";
import Script from "next/script";
import { useCallback, useEffect, useState } from "react";
import Button from "@/components/common/Button";
import ReviewModal from "@/components/review/ReviewModal";
import { ORDER_STATUS_LABEL } from "@/constants/orderStatus";
import { orderApi, paymentApi } from "@/lib/api";
import { getToken } from "@/lib/auth";
import { formatDateTime, formatOptions, formatPrice } from "@/lib/format";
import { AuthRequiredError, errorMessage } from "@/lib/http";
import type { OrderDetail, OrderItem } from "@/types/order";
import styles from "./page.module.css";

const IMP_MERCHANT_CODE = process.env.NEXT_PUBLIC_IAMPORT_MERCHANT_CODE ?? "";
const IMP_PG = process.env.NEXT_PUBLIC_IAMPORT_PG || "nice";

export default function OrderDetailPage() {
  const router = useRouter();
  const params = useParams();
  const ordersId = Number(params.ordersId);

  const [order, setOrder] = useState<OrderDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessageText, setErrorMessageText] = useState<string | null>(null);
  const [isCanceling, setIsCanceling] = useState(false);
  const [isPaying, setIsPaying] = useState(false);
  const [reviewingItem, setReviewingItem] = useState<OrderItem | null>(null);
  // 백엔드 주문 응답에 리뷰 작성 여부가 없으므로, 이 화면에서 작성한 항목만 기억한다
  const [reviewedIds, setReviewedIds] = useState<Set<number>>(new Set());

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

  /** 주문 조회. 최초 로딩 상태는 isLoading 초기값(true)이 담당한다 */
  const fetchOrder = useCallback(
    () =>
      orderApi
        .detail(ordersId)
        .then((data) => {
          setErrorMessageText(null);
          setOrder(data);
        })
        .catch((error: unknown) => {
          console.error(error);
          if (handleAuthError(error)) return;
          setErrorMessageText("주문 정보를 불러오지 못했습니다.");
        })
        .finally(() => setIsLoading(false)),
    [ordersId, handleAuthError],
  );

  const isValidId = Number.isInteger(ordersId) && ordersId > 0;

  useEffect(() => {
    if (!getToken()) {
      router.replace("/login");
      return;
    }
    if (!isValidId) return;
    fetchOrder();
  }, [isValidId, fetchOrder, router]);

  if (!isValidId) {
    return (
      <div className={styles.state}>
        <span>잘못된 주문 번호입니다.</span>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className={styles.state}>
        <span>주문 정보를 불러오는 중...</span>
      </div>
    );
  }

  if (errorMessageText || !order) {
    return (
      <div className={styles.state}>
        <span>{errorMessageText ?? "주문 정보를 불러올 수 없습니다."}</span>
      </div>
    );
  }

  const isPayable = order.orderStatus === "CREATED";
  const canWriteReview = order.orderStatus === "DELIVERED";

  async function handlePay() {
    if (!order) return;

    if (!IMP_MERCHANT_CODE) {
      alert("결제 설정이 없습니다. NEXT_PUBLIC_IAMPORT_MERCHANT_CODE 환경 변수를 확인해주세요.");
      return;
    }

    if (!window.IMP) {
      alert("결제 모듈이 아직 로드되지 않았습니다. 잠시 후 다시 시도해주세요.");
      return;
    }

    try {
      setIsPaying(true);

      // 1단계 · 결제 준비 (검증)
      const intent = await paymentApi.prepare(order.id);

      // 2단계 · Iamport 결제창 호출
      window.IMP.init(IMP_MERCHANT_CODE);

      const items = order.orderItems;
      const orderName =
        items.length > 1
          ? `${items[0].productName} 외 ${items.length - 1}건`
          : items[0]?.productName ?? "주문 상품";

      const pgResponse = await new Promise<IamportResponse>((resolve) => {
        window.IMP!.request_pay(
          {
            pg: IMP_PG,
            pay_method: "card",
            merchant_uid: intent.merchantUid,
            name: orderName,
            amount: Number(intent.amount),
            buyer_name: intent.buyerName ?? "",
            buyer_email: intent.buyerEmail ?? "",
          },
          resolve,
        );
      });

      if (!pgResponse.success || !pgResponse.imp_uid) {
        alert(pgResponse.error_msg || "결제가 취소되었거나 실패했습니다.");
        return;
      }

      // 3단계 · 결제 검증
      const verify = await paymentApi.verify({
        impUid: pgResponse.imp_uid,
        merchantUid: intent.merchantUid,
        amount: Number(intent.amount),
      });

      if (!verify.success || verify.payStatus !== "PAID") {
        alert(verify.message || "결제 검증에 실패했습니다.");
        return;
      }

      // 4단계 · 결제 확인 (포인트/재고 확정)
      const confirmResult = await paymentApi.confirm({
        impUid: pgResponse.imp_uid,
        merchantUid: intent.merchantUid,
      });

      alert(
        `결제가 완료되었습니다.\n적립 포인트: ${formatPrice(confirmResult.earnedPoint)}P`,
      );
      await fetchOrder();
    } catch (error) {
      console.error(error);
      if (handleAuthError(error)) return;
      alert(errorMessage(error, "결제 처리 중 오류가 발생했습니다."));
    } finally {
      setIsPaying(false);
    }
  }

  async function handleCancel() {
    if (!order) return;
    if (!confirm("주문을 취소하시겠습니까?")) return;

    try {
      setIsCanceling(true);
      await orderApi.requestCancel(order.id);
      alert("주문 취소가 요청되었습니다.");
      await fetchOrder();
    } catch (error) {
      console.error(error);
      if (handleAuthError(error)) return;
      alert(errorMessage(error, "주문 취소에 실패했습니다."));
    } finally {
      setIsCanceling(false);
    }
  }

  function handleReviewSubmitted() {
    if (reviewingItem) {
      const id = reviewingItem.orderItemId;
      setReviewedIds((prev) => new Set(prev).add(id));
    }
    setReviewingItem(null);
  }

  const isBusy = isCanceling || isPaying;
  const totalQuantity = order.orderItems.reduce((sum, i) => sum + i.quantity, 0);

  return (
    <>
      <Script
        src="https://cdn.iamport.kr/v1/iamport.js"
        strategy="afterInteractive"
      />
      <section className={styles.section}>
        <div className={styles.container}>
          <header className={styles.pageHeader}>
            <h1 className={styles.title}>주문서</h1>
            <div className={styles.metaRow}>
              <span className={styles.merchantUid}>
                주문번호 {order.merchantUid ?? order.id}
              </span>
              <span className={styles.statusBadge}>
                {ORDER_STATUS_LABEL[order.orderStatus] ?? order.orderStatus}
              </span>
            </div>
            <p className={styles.orderedAt}>
              주문일시 · {formatDateTime(order.orderDate)}
            </p>
          </header>

          <div className={styles.block}>
            <h2 className={styles.blockTitle}>
              주문 상품 ({totalQuantity}개)
            </h2>
            <ul className={styles.itemList}>
              {order.orderItems.map((item) => (
                <li key={item.orderItemId} className={styles.item}>
                  <div className={styles.itemInfo}>
                    <span className={styles.itemName}>{item.productName}</span>
                    <p className={styles.itemOption}>
                      {formatOptions(item.optionName)}
                    </p>
                    <p className={styles.itemSub}>
                      {formatPrice(item.unitPrice)}원 · {item.quantity}개
                    </p>
                  </div>
                  <div className={styles.itemRight}>
                    <div className={styles.itemTotal}>
                      {formatPrice(item.totalPrice)}원
                    </div>
                    {canWriteReview &&
                      (reviewedIds.has(item.orderItemId) ? (
                        <span className={styles.reviewDone}>리뷰 작성 완료</span>
                      ) : (
                        <Button
                          variant="secondary"
                          className={styles.reviewButton}
                          onClick={() => setReviewingItem(item)}
                        >
                          리뷰 작성하기
                        </Button>
                      ))}
                  </div>
                </li>
              ))}
            </ul>
          </div>

          <div className={styles.block}>
            <h2 className={styles.blockTitle}>결제 정보</h2>
            <div className={styles.paymentBox}>
              <div className={styles.paymentRow}>
                <span>상품 금액</span>
                <span>{formatPrice(order.orderTotalPrice)}원</span>
              </div>
              <div className={styles.paymentRow}>
                <span>사용 포인트</span>
                <span>- {formatPrice(order.usedPoint)}P</span>
              </div>
              <div className={styles.paymentRow}>
                <span>적립 예정 포인트</span>
                <span>{formatPrice(order.earnPoint)}P</span>
              </div>
              <div className={styles.paymentDivider} />
              <div className={styles.paymentTotal}>
                <span>최종 결제 금액</span>
                <span className={styles.paymentTotalPrice}>
                  {formatPrice(order.payAmount)}원
                </span>
              </div>
            </div>
          </div>

          {isPayable && (
            <div className={styles.actions}>
              <Button
                variant="secondary"
                className={styles.cancelButton}
                onClick={handleCancel}
                disabled={isBusy}
              >
                {isCanceling ? "취소 요청 중..." : "주문하지 않기"}
              </Button>
              <Button
                variant="primary"
                className={styles.payButton}
                onClick={handlePay}
                disabled={isBusy}
              >
                {isPaying ? "결제 처리 중..." : "결제하기"}
              </Button>
            </div>
          )}
        </div>
      </section>

      {reviewingItem && (
        <ReviewModal
          orderItemId={reviewingItem.orderItemId}
          productName={reviewingItem.productName}
          optionSummary={formatOptions(reviewingItem.optionName)}
          onClose={() => setReviewingItem(null)}
          onSubmitted={handleReviewSubmitted}
        />
      )}
    </>
  );
}
