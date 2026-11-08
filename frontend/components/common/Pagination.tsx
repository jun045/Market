import styles from './Pagination.module.css';

interface PaginationProps {
  currentPage: number;
  totalPage: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({ currentPage, totalPage, onPageChange }: PaginationProps) {
  if (totalPage <= 1) return null;

  const pages = buildPageNumbers(currentPage, totalPage);

  return (
    <nav className={styles.pagination}>
      <button
        className={styles.arrow}
        disabled={currentPage === 1}
        onClick={() => onPageChange(currentPage - 1)}
      >
        ← 이전
      </button>

      <div className={styles.pages}>
        {pages.map((p, i) =>
          p === '...' ? (
            <span key={`dot-${i}`} className={styles.dots}>
              …
            </span>
          ) : (
            <button
              key={p}
              className={`${styles.page} ${p === currentPage ? styles.active : ''}`}
              onClick={() => onPageChange(p as number)}
            >
              {p}
            </button>
          ),
        )}
      </div>

      <button
        className={styles.arrow}
        disabled={currentPage === totalPage}
        onClick={() => onPageChange(currentPage + 1)}
      >
        다음 →
      </button>
    </nav>
  );
}

/** 현재 페이지 전후 2페이지 + 처음/끝을 보여주는 간단한 로직 */
function buildPageNumbers(current: number, total: number): (number | '...')[] {
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  const pages: (number | '...')[] = [1];

  if (current > 3) pages.push('...');

  const start = Math.max(2, current - 1);
  const end = Math.min(total - 1, current + 1);
  for (let i = start; i <= end; i++) pages.push(i);

  if (current < total - 2) pages.push('...');

  pages.push(total);
  return pages;
}
