ALTER TABLE IF EXISTS shogun.userinstancepermissions RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun.userclasspermissions RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun.groupinstancepermissions RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun.groupclasspermissions RENAME COLUMN permissions_id TO permission_id;

ALTER TABLE IF EXISTS shogun_rev.userinstancepermissions_rev RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun_rev.userclasspermissions_rev RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun_rev.groupinstancepermissions_rev RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun_rev.groupclasspermissions_rev RENAME COLUMN permissions_id TO permission_id;
ALTER TABLE IF EXISTS shogun_rev.userinstancepermissions_rev RENAME COLUMN permissions_mod TO permission_mod;
ALTER TABLE IF EXISTS shogun_rev.userclasspermissions_rev RENAME COLUMN permissions_mod TO permission_mod;
ALTER TABLE IF EXISTS shogun_rev.groupinstancepermissions_rev RENAME COLUMN permissions_mod TO permission_mod;
ALTER TABLE IF EXISTS shogun_rev.groupclasspermissions_rev RENAME COLUMN permissions_mod TO permission_mod;
