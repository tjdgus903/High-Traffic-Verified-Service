-- outbox payload 임시 text로 변경 (hibernate-types 제거용)
alter table outbox_events
  alter column payload type text;
