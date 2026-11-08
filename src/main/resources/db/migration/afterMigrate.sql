-- Flyway 콜백: 애플리케이션 시작 시 마이그레이션 후 매번 실행된다.
-- CSV 더미데이터처럼 id를 직접 지정해 넣은 경우 시퀀스가 갱신되지 않아
-- 이후 INSERT가 PK 중복으로 실패하므로, 모든 id 시퀀스를 테이블의 최대 id 이상으로 맞춘다.
-- 시퀀스를 뒤로 되돌리지는 않으므로 여러 번 실행해도 안전하다.
DO $$
DECLARE
    r       record;
    max_id  bigint;
    cur_val bigint;
BEGIN
    FOR r IN
        SELECT s.oid AS seq_oid, n.nspname AS schema_name, t.relname AS table_name, a.attname AS column_name
        FROM pg_class s
        JOIN pg_depend d    ON d.objid = s.oid AND d.deptype IN ('a', 'i')
        JOIN pg_class t     ON t.oid = d.refobjid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = d.refobjsubid
        WHERE s.relkind = 'S'
          AND n.nspname = current_schema()
    LOOP
        EXECUTE format('SELECT MAX(%I) FROM %I.%I', r.column_name, r.schema_name, r.table_name) INTO max_id;
        EXECUTE format('SELECT last_value FROM %s', r.seq_oid::regclass) INTO cur_val;

        IF max_id IS NOT NULL AND max_id > cur_val THEN
            PERFORM setval(r.seq_oid::regclass, max_id, true);
        END IF;
    END LOOP;
END $$;
