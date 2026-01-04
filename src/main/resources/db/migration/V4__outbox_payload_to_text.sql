-- outbox payload 임시 text로 변경 (hibernate-types 제거용)
ALTER TABLE outbox_events
  ALTER COLUMN payload TYPE text
  USING payload::text;