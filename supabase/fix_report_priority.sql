-- Backfill report priority to match the Android app category rules.
-- Run in Supabase Dashboard → SQL Editor after deploying the app fix.

UPDATE public.reports
SET
  priority = CASE trim(split_part(tags, ',', 1))
    WHEN 'Public Safety' THEN 'High Priority'
    WHEN 'Traffic' THEN 'High Priority'
    WHEN 'Water & Drainage' THEN 'High Priority'
    WHEN 'Roads & Infrastructure' THEN 'Medium Priority'
    WHEN 'Lighting' THEN 'Medium Priority'
    ELSE 'Low Priority'
  END,
  tags = trim(split_part(tags, ',', 1)) || ', ' || CASE trim(split_part(tags, ',', 1))
    WHEN 'Public Safety' THEN 'High Priority'
    WHEN 'Traffic' THEN 'High Priority'
    WHEN 'Water & Drainage' THEN 'High Priority'
    WHEN 'Roads & Infrastructure' THEN 'Medium Priority'
    WHEN 'Lighting' THEN 'Medium Priority'
    ELSE 'Low Priority'
  END;
