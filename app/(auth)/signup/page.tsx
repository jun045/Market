'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { memberApi } from '@/lib/api';
import { errorMessage } from '@/lib/http';
import styles from './page.module.css';

interface FormData {
  username: string;
  password: string;
  passwordConfirm: string;
  name: string;
  nickname: string;
  email: string;
}

/* 백엔드 CreateUserSignupRequest 검증 규칙과 동일 */
const LOGIN_ID_PATTERN = /^[a-zA-Z0-9_-]+$/;
const PASSWORD_PATTERN =
  /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*()_+=\-{}[\]:;"'<>,.?/]).+$/;
const NAME_PATTERN = /^[a-zA-Z가-힣0-9_-]+$/;
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function SignupPage() {
  const [form, setForm] = useState<FormData>({
    username: '',
    password: '',
    passwordConfirm: '',
    name: '',
    nickname: '',
    email: '',
  });
  const [errors, setErrors] = useState<Partial<FormData>>({});
  const [submitError, setSubmitError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();

  function update(field: keyof FormData, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: '' }));
  }

  function validate(): boolean {
    const next: Partial<FormData> = {};

    if (!form.username.trim()) next.username = '아이디를 입력해주세요.';
    else if (form.username.length < 4 || form.username.length > 20)
      next.username = '아이디는 4~20자여야 합니다.';
    else if (!LOGIN_ID_PATTERN.test(form.username))
      next.username = '아이디는 영문, 숫자, _, - 만 사용할 수 있습니다.';

    if (!form.password) next.password = '비밀번호를 입력해주세요.';
    else if (form.password.length < 15 || form.password.length > 30)
      next.password = '비밀번호는 15~30자여야 합니다.';
    else if (!PASSWORD_PATTERN.test(form.password))
      next.password = '대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다.';

    if (!form.passwordConfirm) next.passwordConfirm = '비밀번호를 한 번 더 입력해주세요.';
    else if (form.password !== form.passwordConfirm)
      next.passwordConfirm = '비밀번호가 일치하지 않습니다.';

    if (!form.name.trim()) next.name = '이름을 입력해주세요.';
    else if (!NAME_PATTERN.test(form.name))
      next.name = '이름은 한글, 영문, 숫자, _, - 만 사용할 수 있습니다.';

    if (!form.nickname.trim()) next.nickname = '닉네임을 입력해주세요.';
    else if (form.nickname.length < 2 || form.nickname.length > 20)
      next.nickname = '닉네임은 2~20자여야 합니다.';
    else if (!NAME_PATTERN.test(form.nickname))
      next.nickname = '닉네임은 한글, 영문, 숫자, _, - 만 사용할 수 있습니다.';

    if (!form.email.trim()) next.email = '이메일을 입력해주세요.';
    else if (!EMAIL_PATTERN.test(form.email))
      next.email = '올바른 이메일 형식이 아닙니다.';

    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitError('');
    if (!validate()) return;

    try {
      setIsSubmitting(true);
      await memberApi.signup({
        loginId: form.username,
        password: form.password,
        name: form.name.trim(),
        nickname: form.nickname.trim(),
        email: form.email.trim(),
      });
      alert('회원가입이 완료되었습니다. 로그인해주세요.');
      router.push('/login');
    } catch (error) {
      console.error('회원가입 실패:', error);
      setSubmitError(errorMessage(error, '회원가입에 실패했습니다.'));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <Link href="/" className={styles.logo}>
          SHOP<span className={styles.logoDot}>.</span>
        </Link>

        <h1 className={styles.heading}>회원가입</h1>

        <form onSubmit={handleSubmit} className={styles.form} noValidate>
          <Field
            id="username"
            label="아이디"
            type="text"
            value={form.username}
            onChange={(v) => update('username', v)}
            placeholder="영문·숫자 4~20자"
            error={errors.username}
            autoComplete="username"
            autoFocus
          />

          <Field
            id="password"
            label="비밀번호"
            type="password"
            value={form.password}
            onChange={(v) => update('password', v)}
            placeholder="대·소문자, 숫자, 특수문자 포함 15자 이상"
            error={errors.password}
            autoComplete="new-password"
          />

          <Field
            id="passwordConfirm"
            label="비밀번호 확인"
            type="password"
            value={form.passwordConfirm}
            onChange={(v) => update('passwordConfirm', v)}
            placeholder="비밀번호를 다시 입력하세요"
            error={errors.passwordConfirm}
            autoComplete="new-password"
          />

          <Field
            id="name"
            label="이름"
            type="text"
            value={form.name}
            onChange={(v) => update('name', v)}
            placeholder="실명을 입력하세요"
            error={errors.name}
            autoComplete="name"
          />

          <Field
            id="nickname"
            label="닉네임"
            type="text"
            value={form.nickname}
            onChange={(v) => update('nickname', v)}
            placeholder="2~20자 닉네임"
            error={errors.nickname}
            autoComplete="nickname"
          />

          <Field
            id="email"
            label="이메일"
            type="email"
            value={form.email}
            onChange={(v) => update('email', v)}
            placeholder="example@email.com"
            error={errors.email}
            autoComplete="email"
          />

          {submitError && <span className={styles.fieldError}>{submitError}</span>}

          <button type="submit" className={styles.submitButton} disabled={isSubmitting}>
            {isSubmitting ? '가입 처리 중...' : '가입하기'}
          </button>
        </form>

        <p className={styles.loginPrompt}>
          이미 계정이 있으신가요?{' '}
          <Link href="/login" className={styles.loginLink}>
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}

/* ── 재사용 필드 컴포넌트 ── */
interface FieldProps {
  id: string;
  label: string;
  type: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  error?: string;
  autoComplete?: string;
  autoFocus?: boolean;
}

function Field({ id, label, type, value, onChange, placeholder, error, autoComplete, autoFocus }: FieldProps) {
  return (
    <div className={styles.field}>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <input
        id={id}
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className={`${styles.input} ${error ? styles.inputError : ''}`}
        autoComplete={autoComplete}
        autoFocus={autoFocus}
      />
      {error && <span className={styles.fieldError}>{error}</span>}
    </div>
  );
}
