ALTER TABLE IF EXISTS shogun.applications ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.groups ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.layers ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.users ADD COLUMN IF NOT EXISTS uuid uuid;

ALTER TABLE IF EXISTS shogun.permissions ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.groupclasspermissions ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.groupinstancepermissions ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.userclasspermissions ADD COLUMN IF NOT EXISTS uuid uuid;
ALTER TABLE IF EXISTS shogun.userinstancepermissions ADD COLUMN IF NOT EXISTS uuid uuid;

ALTER TABLE IF EXISTS shogun.files RENAME COLUMN file_uuid TO uuid;
ALTER TABLE IF EXISTS shogun.imagefiles RENAME COLUMN file_uuid TO uuid;

UPDATE shogun.applications SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.groups SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.layers SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.users SET uuid = gen_random_uuid() WHERE uuid IS NULL;

UPDATE shogun.permissions SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.groupclasspermissions SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.groupinstancepermissions SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.userclasspermissions SET uuid = gen_random_uuid() WHERE uuid IS NULL;
UPDATE shogun.userinstancepermissions SET uuid = gen_random_uuid() WHERE uuid IS NULL;
