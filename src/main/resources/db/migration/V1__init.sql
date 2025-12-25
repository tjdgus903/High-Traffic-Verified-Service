BEGIN;

-- 스키마를 명시적으로 선택
CREATE SCHEMA IF NOT EXISTS public;
SET search_path TO public;

-- 1) members
CREATE TABLE IF NOT EXISTS members (
  id bigserial PRIMARY KEY,
  email varchar(255) NOT NULL UNIQUE,
  status varchar(20) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- 2) member_profile
CREATE TABLE IF NOT EXISTS member_profile (
  member_id bigint PRIMARY KEY REFERENCES members(id),
  age_range varchar(20),
  preferred_categories text,
  updated_at timestamptz NOT NULL DEFAULT now()
);

-- 3) processed_events (idempotency)
CREATE TABLE IF NOT EXISTS processed_events (
  event_id varchar(100) PRIMARY KEY,
  processed_at timestamptz NOT NULL DEFAULT now()
);

-- 4) member_monthly_stats
CREATE TABLE IF NOT EXISTS member_monthly_stats (
  member_id bigint NOT NULL REFERENCES members(id),
  yyyymm varchar(6) NOT NULL,
  total_amount bigint NOT NULL DEFAULT 0,
  order_count int NOT NULL DEFAULT 0,
  updated_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY(member_id, yyyymm)
);

-- 5) tier_policy
CREATE TABLE IF NOT EXISTS tier_policy (
  id bigserial PRIMARY KEY,
  valid_from date NOT NULL,
  valid_to date,
  silver_amount bigint NOT NULL,
  silver_count int NOT NULL,
  gold_amount bigint NOT NULL,
  gold_count int NOT NULL,
  platinum_amount bigint NOT NULL,
  platinum_count int NOT NULL
);

-- 6) tier_history
CREATE TABLE IF NOT EXISTS tier_history (
  id bigserial PRIMARY KEY,
  member_id bigint NOT NULL REFERENCES members(id),
  from_tier varchar(20) NOT NULL,
  to_tier varchar(20) NOT NULL,
  reason varchar(200) NOT NULL,
  changed_at timestamptz NOT NULL DEFAULT now()
);

-- 7) point_ledger
CREATE TABLE IF NOT EXISTS point_ledger (
  id bigserial PRIMARY KEY,
  member_id bigint NOT NULL REFERENCES members(id),
  event_id varchar(100) NOT NULL,
  amount int NOT NULL,
  type varchar(30) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(member_id, event_id, type)
);

-- 8) coupons
CREATE TABLE IF NOT EXISTS coupons (
  id bigserial PRIMARY KEY,
  code varchar(50) NOT NULL UNIQUE,
  name varchar(100) NOT NULL,
  discount_type varchar(20) NOT NULL,
  value int NOT NULL,
  active boolean NOT NULL DEFAULT true
);

-- 9) issued_coupons
CREATE TABLE IF NOT EXISTS issued_coupons (
  id bigserial PRIMARY KEY,
  member_id bigint NOT NULL REFERENCES members(id),
  coupon_id bigint NOT NULL REFERENCES coupons(id),
  event_id varchar(100) NOT NULL,
  issued_at timestamptz NOT NULL DEFAULT now(),
  used_at timestamptz,
  UNIQUE(member_id, coupon_id, event_id)
);

-- 10) member_summary
CREATE TABLE IF NOT EXISTS member_summary (
  member_id bigint PRIMARY KEY REFERENCES members(id),
  yyyymm varchar(6) NOT NULL,
  tier varchar(20) NOT NULL,
  total_amount bigint NOT NULL,
  order_count int NOT NULL,
  benefit_rate numeric(5,2) NOT NULL,
  available_coupon_count int NOT NULL,
  updated_at timestamptz NOT NULL DEFAULT now()
);

-- 11) tier_policy seed (중복 방지)
INSERT INTO tier_policy(
  valid_from, valid_to,
  silver_amount, silver_count,
  gold_amount, gold_count,
  platinum_amount, platinum_count
)
SELECT
  DATE '2025-01-01', NULL,
  100000, 3,
  300000, 8,
  600000, 15
WHERE NOT EXISTS (
  SELECT 1 FROM tier_policy WHERE valid_from = DATE '2025-01-01'
);

COMMIT;