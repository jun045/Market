'use client';

import { useState } from 'react';
import SafeImage from '@/components/common/SafeImage';
import { isImageUrl } from '@/lib/format';
import styles from './ImageGallery.module.css';

interface ImageGalleryProps {
  thumb: string | null;
  detailImage: string | null;
  productName: string;
}

export default function ImageGallery({ thumb, detailImage, productName }: ImageGalleryProps) {
  const allImages = [thumb, detailImage].filter(isImageUrl);
  const [selected, setSelected] = useState(0);

  return (
    <div className={styles.gallery}>
      <div className={styles.mainImage}>
        <SafeImage
          src={allImages[selected]}
          alt={productName}
          fallback={<div className={styles.placeholder}>상품 이미지</div>}
        />
      </div>

      {allImages.length > 1 && (
        <div className={styles.thumbList}>
          {allImages.map((src, i) => (
            <button
              key={src}
              className={`${styles.thumbItem} ${i === selected ? styles.thumbActive : ''}`}
              onClick={() => setSelected(i)}
            >
              <SafeImage
                src={src}
                alt={`${productName} ${i + 1}`}
                fallback={<span className={styles.thumbPlaceholder}>{i + 1}</span>}
              />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
