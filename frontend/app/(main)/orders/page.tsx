"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import Pagination from "@/components/common/Pagination";
import { ORDER_STATUS_LABEL } from "@/constants/orderStatus";
import { orderApi, type OrderPage } from "@/lib/api";
import { getToken } from "@/lib/auth";
import { formatDateTime, formatPrice } from "@/lib/format";
import { AuthRequiredError } from "@/lib/http";
import type { OrderSummary } from "@/types/order";
import styles from "./page.module.css";

const PAGE_SIZE = 10;

export default function OrdersListPage() {
  const router = useRouter();

  const [list, setList] = useState<OrderPage | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    if (!getToken()) {
      router.replace("/login");
      return;
    }

    let isMounted = true;

    async function fetchOrders() {
      try {
        setIsLoading(true);
        setErrorMessage(null);
        const data = await orderApi.list(currentPage, PAGE_SIZE);
        if (isMounted) setList(data);
      } catch (error) {
        console.error(error);
        if (error instanceof AuthRequiredError) {
          router.replace("/login");
          return;
        }
        if (isMounted) setErrorMessage("주문 내역을 불러오지 못했습니다.");
      } finally {
        if (isMounted) setIsLoading(false);
      }
    }

    fetchOrders();

    return () => {
      isMounted = false;
    };
  }, [currentPage, router]);

  function handlePageChange(page: number) {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  if (isLoading) {
    return (
      <div className={styles.state}>
        <span>주문 내역을 불러오는 중...</span>
      </div>
    );
  }

  if (errorMessage || !list) {
    return (
      <div className={styles.state}>
        <span>{errorMessage ?? "주문 내역을 불러올 수 없습니다."}</span>
      </div>
    );
  }

  const isEmpty = list.content.length === 0;

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <header className={styles.pageHeader}>
          <h1 className={styles.title}>주문/배송</h1>
          <p className={styles.subtitle}>
            총 {list.totalElement}건의 주문 내역
          </p>
        </header>

        {isEmpty ? (
          <div className={styles.empty}>
            <p className={styles.emptyText}>주문 내역이 없습니다.</p>
            <Link href="/" className={styles.emptyLink}>
              쇼핑하러 가기
            </Link>
          </div>
        ) : (
          <>
            <ul className={styles.orderList}>
              {list.content.map((order) => (
                <OrderCard key={order.id} order={order} />
              ))}
            </ul>

            <Pagination
              currentPage={currentPage}
              totalPage={list.totalPage}
              onPageChange={handlePageChange}
            />
          </>
        )}
      </div>
    </section>
  );
}

function OrderCard({ order }: { order: OrderSummary }) {
  const isCanceled = order.orderStatus === "CANCELED";
  const orderedAt = formatDateTime(order.orderDate);
  const discount = order.orderTotalPrice - order.payAmount;

  return (
    <li className={`${styles.orderCard} ${isCanceled ? styles.deleted : ""}`}>
      <div className={styles.orderHeader}>
        <div className={styles.headerLeft}>
          <span className={styles.statusBadge}>
            {ORDER_STATUS_LABEL[order.orderStatus] ?? order.orderStatus}
          </span>
          <span className={styles.merchantUid}>주문번호 {order.id}</span>
        </div>
        <Link href={`/orders/${order.id}`} className={styles.detailLink}>
          주문서 보기 →
        </Link>
      </div>

      {orderedAt && (
        <ul className={styles.timeline}>
          <li className={styles.timelineItem}>
            <span className={styles.timelineLabel}>주문</span>
            <span className={styles.timelineValue}>{orderedAt}</span>
          </li>
        </ul>
      )}

      <div className={styles.orderFooter}>
        <div className={styles.addressShort}>
          <span className={styles.addressLabel}>상품 금액</span>
          <span>
            {formatPrice(order.orderTotalPrice)}원
            {discount > 0 && ` (포인트 -${formatPrice(discount)}P)`}
          </span>
        </div>
        <div className={styles.footerRight}>
          <div className={styles.totalPrice}>
            <span className={styles.totalLabel}>결제금액</span>
            <span className={styles.totalValue}>
              {formatPrice(order.payAmount)}원
            </span>
          </div>
        </div>
      </div>
    </li>
  );
}
