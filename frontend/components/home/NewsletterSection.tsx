import styles from './NewsletterSection.module.css';

export default function NewsletterSection() {
  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <div className={styles.text}>
          <div className={styles.title}>새로운 소식을 가장 먼저 받아보세요</div>
          <div className={styles.desc}>
            신상품, 기획전, 쿠폰 정보를 이메일로 받아볼 수 있습니다.
          </div>
        </div>
        <button className={styles.button}>뉴스레터 구독하기</button>
      </div>
    </section>
  );
}
