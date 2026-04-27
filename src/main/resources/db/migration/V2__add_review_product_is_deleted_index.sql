create index if not exists idx_review_product_deleted_created
on public.review(product_id, is_deleted, created_at desc);