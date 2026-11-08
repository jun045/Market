"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { memberApi } from "@/lib/api";
import { setToken } from "@/lib/auth";
import { errorMessage } from "@/lib/http";
import styles from "./page.module.css";

export default function LoginPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");

    if (!username.trim()) {
      setError("아이디를 입력해주세요.");
      return;
    }
    if (!password) {
      setError("비밀번호를 입력해주세요.");
      return;
    }

    try {
      setIsSubmitting(true);

      const data = await memberApi.login({ loginId: username.trim(), password });

      if (!data?.token) {
        throw new Error("토큰이 응답되지 않았습니다.");
      }

      setToken(data.token);
      router.push("/");
      router.refresh();
    } catch (error) {
      console.error("로그인 실패:", error);
      setError(errorMessage(error, "로그인 중 오류가 발생했습니다."));
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

        <h1 className={styles.heading}>로그인</h1>

        <form onSubmit={handleSubmit} className={styles.form} noValidate>
          <div className={styles.field}>
            <label htmlFor="username" className={styles.label}>
              아이디
            </label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="아이디를 입력하세요"
              className={styles.input}
              autoComplete="username"
              autoFocus
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="password" className={styles.label}>
              비밀번호
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              className={styles.input}
              autoComplete="current-password"
            />
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button
            type="submit"
            className={styles.submitButton}
            disabled={isSubmitting}
          >
            {isSubmitting ? "로그인 중..." : "로그인"}
          </button>
        </form>

        <div className={styles.divider}>
          <span>또는</span>
        </div>

        <div className={styles.links}>
          <span className={styles.link}>아이디 찾기</span>
          <span className={styles.dot}>·</span>
          <span className={styles.link}>비밀번호 찾기</span>
          <span className={styles.dot}>·</span>
          <Link href="/signup" className={styles.linkPrimary}>
            회원가입
          </Link>
        </div>
      </div>
    </div>
  );
}
