"use client";

import { useEffect, useState } from "react";
import Button from "@/components/common/Button";
import { reviewApi } from "@/lib/api";
import { getToken } from "@/lib/auth";
import { errorMessage } from "@/lib/http";
import styles from "./ReviewModal.module.css";

interface ReviewModalProps {
  orderItemId: number;
  productName: string;
  optionSummary: string;
  onClose: () => void;
  onSubmitted: () => void;
}

const MIN_LENGTH = 10;
const MAX_LENGTH = 300;

export default function ReviewModal({
  orderItemId,
  productName,
  optionSummary,
  onClose,
  onSubmitted,
}: ReviewModalProps) {
  const [rating, setRating] = useState<number>(5);
  const [hoveredRating, setHoveredRating] = useState<number>(0);
  const [content, setContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const prevOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prevOverflow;
    };
  }, []);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape" && !isSubmitting) onClose();
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [isSubmitting, onClose]);

  async function handleSubmit() {
    const trimmed = content.trim();
    if (rating < 1 || rating > 5) {
      alert("별점을 선택해주세요.");
      return;
    }
    if (trimmed.length < MIN_LENGTH) {
      alert(`리뷰는 ${MIN_LENGTH}자 이상 입력해주세요.`);
      return;
    }
    if (trimmed.length > MAX_LENGTH) {
      alert(`리뷰는 ${MAX_LENGTH}자 이내로 입력해주세요.`);
      return;
    }

    if (!getToken()) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      setIsSubmitting(true);
      await reviewApi.create(orderItemId, { rating, content: trimmed });
      alert("리뷰가 등록되었습니다.");
      onSubmitted();
    } catch (error) {
      console.error(error);
      alert(errorMessage(error, "리뷰 등록에 실패했습니다."));
    } finally {
      setIsSubmitting(false);
    }
  }

  const currentRating = hoveredRating || rating;

  return (
    <div
      className={styles.backdrop}
      onClick={() => {
        if (!isSubmitting) onClose();
      }}
    >
      <div
        className={styles.modal}
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="review-modal-title"
      >
        <header className={styles.header}>
          <h2 id="review-modal-title" className={styles.title}>
            리뷰 작성
          </h2>
          <button
            type="button"
            className={styles.closeButton}
            onClick={onClose}
            disabled={isSubmitting}
            aria-label="닫기"
          >
            ×
          </button>
        </header>

        <div className={styles.productBox}>
          <p className={styles.productName}>{productName}</p>
          <p className={styles.productOption}>{optionSummary}</p>
        </div>

        <div className={styles.section}>
          <label className={styles.label}>
            별점 <span className={styles.required}>*</span>
          </label>
          <div
            className={styles.stars}
            onMouseLeave={() => setHoveredRating(0)}
          >
            {[1, 2, 3, 4, 5].map((n) => (
              <button
                key={n}
                type="button"
                className={`${styles.star} ${
                  n <= currentRating ? styles.starActive : ""
                }`}
                onClick={() => setRating(n)}
                onMouseEnter={() => setHoveredRating(n)}
                disabled={isSubmitting}
                aria-label={`${n}점`}
              >
                ★
              </button>
            ))}
            <span className={styles.ratingValue}>{rating} / 5</span>
          </div>
        </div>

        <div className={styles.section}>
          <label htmlFor="review-content" className={styles.label}>
            리뷰 작성 <span className={styles.required}>*</span>
          </label>
          <textarea
            id="review-content"
            className={styles.textarea}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="내용을 10자 이상 300자 미만으로 입력해주세요. 주제와 무관한 댓글, 악플 등의 글은 임의 삭제될 수 있습니다."
            maxLength={MAX_LENGTH}
            disabled={isSubmitting}
            rows={8}
          />
          <div className={styles.charCount}>
            {content.length} / {MAX_LENGTH}
          </div>
        </div>

        <div className={styles.footer}>
          <Button
            variant="secondary"
            className={styles.cancelButton}
            onClick={onClose}
            disabled={isSubmitting}
          >
            취소
          </Button>
          <Button
            variant="primary"
            className={styles.submitButton}
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? "등록 중..." : "등록"}
          </Button>
        </div>
      </div>
    </div>
  );
}
