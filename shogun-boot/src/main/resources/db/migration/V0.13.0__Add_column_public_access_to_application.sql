ALTER TABLE IF EXISTS shogun.applications ADD COLUMN IF NOT EXISTS public_access bool;

ALTER TABLE IF EXISTS shogun.applications_rev ADD COLUMN IF NOT EXISTS public_access bool;
ALTER TABLE IF EXISTS shogun.applications_rev ADD COLUMN IF NOT EXISTS public_access_mod bool;
