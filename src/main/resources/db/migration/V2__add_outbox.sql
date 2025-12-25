create table outbox_events (
  id bigserial primary key,
  event_id varchar(100) not null,
  event_type varchar(50) not null,
  payload jsonb not null,
  status varchar(20) not null default 'PENDING',
  retry_count int not null default 0,
  next_retry_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique(event_id, event_type)
);

create index idx_outbox_status_next_retry
  on outbox_events(status, next_retry_at);

create index idx_outbox_created_at
  on outbox_events(created_at);
