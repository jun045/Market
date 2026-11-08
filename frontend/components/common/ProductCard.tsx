import Link from 'next/link';
import SafeImage from '@/components/common/SafeImage';
import { formatPrice } from '@/lib/format';
import { Product } from '@/types/product';
import styles from './ProductCard.module.css';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  return (
    <Link href={`/products/${product.id}`} className={styles.card}>
      <div className={styles.imageWrapper}>
        <SafeImage
          src={product.thumb}
          alt={product.productName}
          fallback={<div className={styles.imagePlaceholder}>상품 이미지</div>}
        />
      </div>
      <div className={styles.info}>
        <div className={styles.brand}>{product.brandName}</div>
        <div className={styles.name}>{product.productName}</div>
        <div className={styles.price}>{formatPrice(product.price)}원</div>
      </div>
    </Link>
  );
}
