-- 부분검색(ILIKE '%keyword%')용 GIN trigram 인덱스 (pg_trgm 익스텐션은 V3에서 생성됨)

CREATE INDEX IF NOT EXISTS idx_brand_name_trgm
ON brand USING GIN (brand_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_login_id_trgm
ON member USING GIN (login_id gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_email_trgm
ON member USING GIN (email gin_trgm_ops);
