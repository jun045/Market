import { formatOptions, formatPrice } from '@/lib/format';
import { ProductVariant } from '@/types/product';
import styles from './VariantSelector.module.css';

interface VariantSelectorProps {
  variants: ProductVariant[];
  /** 상품 정가 — 옵션가와의 차액(추가금/할인)을 표시하는 데 사용 */
  listPrice: number;
  selectedId: number | null;
  onSelect: (variant: ProductVariant) => void;
  disabled?: boolean;
}

export default function VariantSelector({
  variants,
  listPrice,
  selectedId,
  onSelect,
  disabled = false,
}: VariantSelectorProps) {
  return (
    <div className={styles.selector}>
      <div className={styles.label}>옵션 선택</div>
      {variants.length === 0 ? (
        <span className={styles.soldOut}>구매 가능한 옵션이 없습니다.</span>
      ) : (
        <div className={styles.list}>
          {variants.map((v) => {
            const isSelected = v.id === selectedId;
            const diff = v.salePrice - listPrice;

            return (
              <button
                key={v.id}
                className={`${styles.option} ${isSelected ? styles.selected : ''} ${disabled ? styles.disabled : ''}`}
                onClick={() => !disabled && onSelect(v)}
                disabled={disabled}
              >
                <span className={styles.optionName}>{formatOptions(v.options)}</span>
                <span className={styles.optionMeta}>
                  <span className={styles.optionPrice}>{formatPrice(v.salePrice)}원</span>
                  {diff !== 0 && (
                    <span className={styles.extra}>
                      ({diff > 0 ? '+' : '-'}
                      {formatPrice(Math.abs(diff))})
                    </span>
                  )}
                </span>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
