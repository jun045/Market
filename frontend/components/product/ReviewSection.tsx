import Pagination from '@/components/common/Pagination';
import { formatDate, formatOptions } from '@/lib/format';
import type { PageResponse } from '@/types/common';
import type { Review } from '@/types/review';
import styles from './ReviewSection.module.css';

interface ReviewSectionProps {
  data: PageResponse<Review>;
  onPageChange: (page: number) => void;
}

export default function ReviewSection({ data, onPageChange }: ReviewSectionProps) {
  const { content: reviews, pageInfo } = data;

  return (
    <div className={styles.section}>
      <div className={styles.header}>
        <h2 className={styles.title}>상품 리뷰</h2>
        <span className={styles.count}>총 {pageInfo.totalElement}건</span>
      </div>

      {reviews.length === 0 ? (
        <p className={styles.empty}>아직 등록된 리뷰가 없습니다.</p>
      ) : (
        <ul className={styles.list}>
          {reviews.map((review) => (
            <ReviewItem key={review.reviewId} review={review} />
          ))}
        </ul>
      )}

      <Pagination
        currentPage={pageInfo.pageNumber}
        totalPage={pageInfo.totalPage}
        onPageChange={onPageChange}
      />
    </div>
  );
}

/* ── 개별 리뷰 아이템 ── */

function ReviewItem({ review }: { review: Review }) {
  return (
    <li className={styles.item}>
      <div className={styles.itemTop}>
        <div className={styles.userInfo}>
          <span className={styles.nickname}>{review.nickname}</span>
          <span className={styles.option}>{formatOptions(review.option)}</span>
        </div>
        <time className={styles.date}>{formatDate(review.createdAt)}</time>
      </div>

      <div className={styles.stars}>
        {Array.from({ length: 5 }, (_, i) => (
          <span key={i} className={i < review.rating ? styles.starFilled : styles.starEmpty}>
            ★
          </span>
        ))}
      </div>

      <p className={styles.content}>{review.content}</p>
    </li>
  );
}
