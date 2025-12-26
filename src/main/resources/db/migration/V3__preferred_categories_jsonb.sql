-- member_profile.preferred_categories: text -> jsonb
alter table member_profile
  alter column preferred_categories type jsonb
  using to_jsonb(preferred_categories);

-- null 방어 (선택)
update member_profile
set preferred_categories = '[]'::jsonb
where preferred_categories is null;