ALTER TABLE shogun.users
DROP CONSTRAINT IF EXISTS users_unique_auth_provider_id,
ADD CONSTRAINT users_unique_auth_provider_id UNIQUE (auth_provider_id);

ALTER TABLE shogun.groups
DROP CONSTRAINT IF EXISTS groups_unique_auth_provider_id,
ADD CONSTRAINT groups_unique_auth_provider_id UNIQUE (auth_provider_id);