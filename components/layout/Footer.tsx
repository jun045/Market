import Link from 'next/link';
import styles from './Footer.module.css';

export default function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.left}>
          <div className={styles.logo}>
            SHOP<span className={styles.logoDot}>.</span>
          </div>
          <div className={styles.links}>
            <Link href="/support">고객센터</Link>
            <span className={styles.divider}>·</span>
            <Link href="/terms">이용약관</Link>
            <span className={styles.divider}>·</span>
            <Link href="/privacy">개인정보처리방침</Link>
            <span className={styles.divider}>·</span>
            <Link href="/business-info">사업자정보</Link>
          </div>
        </div>
        <div className={styles.copyright}>
          © 2026 Shop. All rights reserved.
        </div>
      </div>
    </footer>
  );
}
