import Link from 'next/link';
import SectionLabel from '@/components/common/SectionLabel';
import { PARENT_CATEGORIES } from '@/constants/categories';
import styles from './CategorySection.module.css';

export default function CategorySection() {
  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <SectionLabel label="CATEGORY" />
        <h2 className={styles.title}>
          원하는 <span className={styles.accent}>카테고리</span>를 선택하세요
        </h2>
        <div className={styles.grid}>
          {PARENT_CATEGORIES.map((cat) => (
            <div key={cat.id} className={styles.cardWrapper}>
              <div className={styles.card}>
                <div className={styles.number}>{cat.no}</div>
                <div className={styles.name}>{cat.name}</div>
              </div>
              <div className={styles.dropdown}>
                {cat.children.map((sub) => (
                  <Link
                    key={sub.id}
                    href={`/products?categoryId=${sub.id}`}
                    className={styles.dropdownItem}
                  >
                    {sub.name}
                  </Link>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
