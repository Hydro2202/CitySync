-- =============================================================================
-- CitySync: Unified auth.users → public.users sync
-- Paste this entire script into Supabase Dashboard → SQL Editor → Run
--
-- Root cause of "Unknown Error" / signup 500:
--   Multiple triggers on auth.users all INSERT into public.users → duplicate PK.
-- This script removes every custom auth.users trigger and installs exactly one.
-- =============================================================================

-- 1. Remove ALL custom triggers on auth.users (not just on_auth_user_created)
DO $$
DECLARE
  trigger_record RECORD;
BEGIN
  FOR trigger_record IN
    SELECT t.tgname
    FROM pg_trigger t
    JOIN pg_class c ON t.tgrelid = c.oid
    JOIN pg_namespace n ON c.relnamespace = n.oid
    WHERE n.nspname = 'auth'
      AND c.relname = 'users'
      AND NOT t.tgisinternal
  LOOP
    EXECUTE format('DROP TRIGGER IF EXISTS %I ON auth.users', trigger_record.tgname);
  END LOOP;
END $$;

-- 2. Drop known legacy trigger functions (safe if they do not exist)
DROP FUNCTION IF EXISTS public.handle_new_user() CASCADE;
DROP FUNCTION IF EXISTS public.signup_copy_to_users_table() CASCADE;
DROP FUNCTION IF EXISTS public.sync_user_to_public() CASCADE;
DROP FUNCTION IF EXISTS public.create_public_user() CASCADE;

-- 3. Single trigger: read metadata keys that match the Android client payload
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO public.users (id, email, full_name, phone, address)
  VALUES (
    NEW.id,
    NEW.email,
    NULLIF(TRIM(COALESCE(NEW.raw_user_meta_data->>'full_name', '')), ''),
    NULLIF(TRIM(COALESCE(NEW.raw_user_meta_data->>'phone', '')), ''),
    NULLIF(TRIM(COALESCE(NEW.raw_user_meta_data->>'address', '')), '')
  )
  ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    full_name = COALESCE(EXCLUDED.full_name, public.users.full_name),
    phone = COALESCE(EXCLUDED.phone, public.users.phone),
    address = COALESCE(EXCLUDED.address, public.users.address);

  RETURN NEW;
END;
$$;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW
  EXECUTE FUNCTION public.handle_new_user();

-- 4. Backfill rows created before metadata keys were aligned
UPDATE public.users AS u
SET
  full_name = COALESCE(
    u.full_name,
    NULLIF(TRIM(au.raw_user_meta_data->>'full_name'), '')
  ),
  phone = COALESCE(
    u.phone,
    NULLIF(TRIM(au.raw_user_meta_data->>'phone'), '')
  ),
  address = COALESCE(
    u.address,
    NULLIF(TRIM(au.raw_user_meta_data->>'address'), '')
  )
FROM auth.users AS au
WHERE u.id = au.id
  AND (
    u.full_name IS NULL
    OR u.phone IS NULL
    OR u.address IS NULL
  );

-- 5. Verify: should return exactly ONE row named on_auth_user_created
SELECT t.tgname AS trigger_name, p.proname AS function_name
FROM pg_trigger t
JOIN pg_class c ON t.tgrelid = c.oid
JOIN pg_namespace n ON c.relnamespace = n.oid
JOIN pg_proc p ON t.tgfoid = p.oid
WHERE n.nspname = 'auth'
  AND c.relname = 'users'
  AND NOT t.tgisinternal;
