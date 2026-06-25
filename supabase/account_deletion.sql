-- =============================================================================
-- CitySync: Account deletion — remove user reports and profile
-- Run in Supabase Dashboard → SQL Editor
-- =============================================================================

-- 1. Cascade-delete reports when an auth user is removed
ALTER TABLE public.reports
  DROP CONSTRAINT IF EXISTS reports_user_id_fkey;

ALTER TABLE public.reports
  ADD CONSTRAINT reports_user_id_fkey
  FOREIGN KEY (user_id)
  REFERENCES auth.users(id)
  ON DELETE CASCADE;

-- 2. RLS: allow users to delete their own reports (client-side cleanup)
ALTER TABLE public.reports ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can delete own reports" ON public.reports;
CREATE POLICY "Users can delete own reports"
  ON public.reports
  FOR DELETE
  TO authenticated
  USING (auth.uid() = user_id);

-- 3. RLS: allow users to delete their own profile row
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can delete own profile" ON public.users;
CREATE POLICY "Users can delete own profile"
  ON public.users
  FOR DELETE
  TO authenticated
  USING (auth.uid() = id);

-- 4. Server-side account deletion (reports → profile → auth user)
CREATE OR REPLACE FUNCTION public.delete_user_account()
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth
AS $$
DECLARE
  uid uuid := auth.uid();
BEGIN
  IF uid IS NULL THEN
    RAISE EXCEPTION 'Not authenticated';
  END IF;

  DELETE FROM public.reports WHERE user_id = uid;
  DELETE FROM public.users WHERE id = uid;
  DELETE FROM auth.users WHERE id = uid;
END;
$$;

REVOKE ALL ON FUNCTION public.delete_user_account() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.delete_user_account() TO authenticated;
