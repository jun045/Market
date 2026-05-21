CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_product_name_trgm
ON product USING GIN (product_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_member_email
ON member (email);