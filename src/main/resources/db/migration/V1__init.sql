-- Flyway V1: 초기 스키마
-- 원칙:
-- 1) 한번 적용된 V1은 가급적 수정하지 않는다.
-- 2) 변경이 생기면 V2, V3 ... 새 파일로 추가한다.

create table members (
  id bigserial primary key,
  email varchar(255) not null unique,
  status varchar(20) not null,
  created_at timestamptz not null default now()
);

create table member_profile (
  member_id bigint primary key references members(id),
  age_range varchar(20),
  preferred_categories text,
  updated_at timestamptz not null default now()
);

-- "이벤트 중복 처리 방지"를 위한 테이블 (idempotency 핵심)
create table processed_events (
  event_id varchar(100) primary key,
  processed_at timestamptz not null default now()
);

-- 월 누적 집계 (등급 산정의 근거)
create table member_monthly_stats (
  member_id bigint not null references members(id),
  yyyymm varchar(6) not null,
  total_amount bigint not null default 0,
  order_count int not null default 0,
  updated_at timestamptz not null default now(),
  primary key(member_id, yyyymm)
);

-- 등급 정책(버전 관리 가능하게 기간을 둠)
create table tier_policy (
  id bigserial primary key,
  valid_from date not null,
  valid_to date,
  silver_amount bigint not null,
  silver_count int not null,
  gold_amount bigint not null,
  gold_count int not null,
  platinum_amount bigint not null,
  platinum_count int not null
);

create table tier_history (
  id bigserial primary key,
  member_id bigint not null references members(id),
  from_tier varchar(20) not null,
  to_tier varchar(20) not null,
  reason varchar(200) not null,
  changed_at timestamptz not null default now()
);

-- 포인트 원장(ledger): 이벤트 단위로 기록하여 추적/정합성 확보
create table point_ledger (
  id bigserial primary key,
  member_id bigint not null references members(id),
  event_id varchar(100) not null,
  amount int not null,
  type varchar(30) not null,
  created_at timestamptz not null default now(),
  unique(member_id, event_id, type)
);

create table coupons (
  id bigserial primary key,
  code varchar(50) not null unique,
  name varchar(100) not null,
  discount_type varchar(20) not null,
  value int not null,
  active boolean not null default true
);

create table issued_coupons (
  id bigserial primary key,
  member_id bigint not null references members(id),
  coupon_id bigint not null references coupons(id),
  event_id varchar(100) not null,
  issued_at timestamptz not null default now(),
  used_at timestamptz,
  unique(member_id, coupon_id, event_id)
);

-- 조회 최적화(Read Model): summary API가 조인 없이 빠르게 응답하도록
create table member_summary (
  member_id bigint primary key references members(id),
  yyyymm varchar(6) not null,
  tier varchar(20) not null,
  total_amount bigint not null,
  order_count int not null,
  benefit_rate numeric(5,2) not null,
  available_coupon_count int not null,
  updated_at timestamptz not null default now()
);

-- 기본 정책 seed (예시)
insert into tier_policy(valid_from, valid_to, silver_amount, silver_count, gold_amount, gold_count, platinum_amount, platinum_count)
values ('2025-01-01', null, 100000, 3, 300000, 8, 600000, 15);