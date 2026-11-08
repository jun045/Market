import SectionLabel from '@/components/common/SectionLabel';
import styles from './FeatureSection.module.css';

const features = [
  {
    no: '01',
    title: '검증된 셀러만',
    description:
      '엄격한 심사를 거친 신뢰할 수 있는 브랜드와 셀러만 입점합니다.',
  },
  {
    no: '02',
    title: '빠른 배송',
    description: '오후 3시 이전 주문 시 당일 출고, 전국 익일 배송을 보장합니다.',
  },
  {
    no: '03',
    title: '안심 구매 보장',
    description: '7일 내 무료 교환·반품, 100% 정품 인증 제도를 운영합니다.',
  },
];

export default function FeatureSection() {
  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <SectionLabel label="WHY SHOP WITH US" />
        <h2 className={styles.title}>
          믿고 구매할 수 있는 <span className={styles.accent}>3가지 이유</span>
        </h2>
        <div className={styles.grid}>
          {features.map((f) => (
            <div key={f.no} className={styles.card}>
              <div className={styles.number}>{f.no}</div>
              <div className={styles.cardTitle}>{f.title}</div>
              <div className={styles.cardDesc}>{f.description}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
