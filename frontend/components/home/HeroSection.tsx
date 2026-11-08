import SectionLabel from '@/components/common/SectionLabel';
import styles from './HeroSection.module.css';

export default function HeroSection() {
  return (
    <section className={styles.hero}>
      <div className={styles.container}>
        <SectionLabel label="NEW ARRIVALS" />
        <h1 className={styles.title}>
          일상을 바꾸는 <span className={styles.accent}>좋은 선택</span>,
          <br />
          오늘부터 시작됩니다
        </h1>
        <p className={styles.description}>
          엄선된 브랜드와 검증된 상품만 모아, 믿을 수 있는 쇼핑 경험을
          제공합니다.
        </p>
        <form className={styles.searchForm} action="/products" method="get">
          <input
            className={styles.searchInput}
            type="text"
            name="keyword"
            placeholder="상품명, 브랜드, 카테고리를 검색해보세요"
          />
          <button type="submit" className={styles.searchButton}>
            검색
          </button>
        </form>
      </div>
    </section>
  );
}
