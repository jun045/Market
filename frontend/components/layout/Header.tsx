'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { cartApi } from '@/lib/api';
import {
  AUTH_CHANGE_EVENT,
  CART_CHANGE_EVENT,
  clearToken,
  getToken,
} from '@/lib/auth';
import styles from './Header.module.css';

export default function Header() {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    let isMounted = true;

    const syncCart = async () => {
      if (!getToken()) {
        setCartCount(0);
        return;
      }
      try {
        const data = await cartApi.get(1, 1);
        if (isMounted) setCartCount(data.cartResponse.totalQuantity);
      } catch {
        if (isMounted) setCartCount(0);
      }
    };

    const syncAuth = () => {
      setIsLoggedIn(!!getToken());
      syncCart();
    };

    syncAuth();
    window.addEventListener('storage', syncAuth);
    window.addEventListener(AUTH_CHANGE_EVENT, syncAuth);
    window.addEventListener(CART_CHANGE_EVENT, syncCart);
    return () => {
      isMounted = false;
      window.removeEventListener('storage', syncAuth);
      window.removeEventListener(AUTH_CHANGE_EVENT, syncAuth);
      window.removeEventListener(CART_CHANGE_EVENT, syncCart);
    };
  }, []);

  const handleLogout = () => {
    clearToken();
    router.push('/');
    router.refresh();
  };

  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <div className={styles.left}>
          <Link href="/" className={styles.logo}>
            SHOP<span className={styles.logoDot}>.</span>
          </Link>
          <nav className={styles.nav}>
            <span className={styles.navDisabled}>전체 카테고리</span>
            <span className={styles.navDisabled}>신상품</span>
            <Link href="/products">베스트</Link>
            <span className={styles.navDisabled}>기획전</span>
            <span className={styles.navDisabled}>브랜드</span>
          </nav>
        </div>

        <div className={styles.right}>
          <Link href="/">홈</Link>
          <span className={styles.navDisabled}>마이페이지</span>
          <Link href="/cart">장바구니 ({cartCount})</Link>
          {isLoggedIn && <Link href="/orders">주문/배송</Link>}
          {isLoggedIn ? (
            <button
              type="button"
              onClick={handleLogout}
              className={styles.logoutButton}
            >
              로그아웃
            </button>
          ) : (
            <>
              <Link href="/login" className={styles.loginLink}>
                로그인
              </Link>
              <Link href="/signup" className={styles.signupLink}>
                회원가입
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
