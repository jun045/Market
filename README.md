# Market_front

`Market` 백엔드(Spring Boot)를 위한 프론트엔드입니다. (Next.js 16 · React 19 · TypeScript)

## 시작하기

```bash
npm install
cp .env.example .env.local   # 값 수정
npm run dev                  # http://localhost:3000
```

백엔드는 `BACKEND_URL`(기본 `http://localhost:8080`)에서 실행 중이어야 합니다.
브라우저는 `/api/*`로 요청하고 Next.js가 백엔드로 프록시하므로 별도 CORS 설정이 필요 없습니다.

## 환경 변수

| 이름 | 설명 |
| --- | --- |
| `BACKEND_URL` | 백엔드 서버 주소 (서버 전용) |
| `NEXT_PUBLIC_IAMPORT_MERCHANT_CODE` | 아임포트 가맹점 식별코드 (백엔드 `IMPORT_CLIENT_KEY`와 동일) |
| `NEXT_PUBLIC_IAMPORT_PG` | 아임포트 PG 코드 (기본 `nice`) |
