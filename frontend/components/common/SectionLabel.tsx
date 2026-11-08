import styles from './SectionLabel.module.css';

interface SectionLabelProps {
  label: string;
}

export default function SectionLabel({ label }: SectionLabelProps) {
  return (
    <div className={styles.wrapper}>
      <span className={styles.line} />
      <span className={styles.text}>{label}</span>
    </div>
  );
}
