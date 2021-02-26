INSERT INTO permissions (id, created, modified, name)
SELECT
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ_UPDATE_DELETE'
WHERE
  NOT EXISTS (
    SELECT
      *
    FROM
      permissions
    WHERE
      name = 'READ_UPDATE_DELETE'
  );

INSERT INTO permission (permissions_id, permissions)
SELECT
  currval('hibernate_sequence'),
  'READ'
WHERE
  NOT EXISTS (
    SELECT
      *
    FROM
      permission
    WHERE
      permissions_id = currval('hibernate_sequence') AND
      permissions = 'READ'
  );

INSERT INTO permission (permissions_id, permissions)
SELECT
  currval('hibernate_sequence'),
  'UPDATE'
WHERE
  NOT EXISTS (
    SELECT
      *
    FROM
      permission
    WHERE
      permissions_id = currval('hibernate_sequence') AND
      permissions = 'UPDATE'
  );

INSERT INTO permission (permissions_id, permissions)
SELECT
  currval('hibernate_sequence'),
  'DELETE'
WHERE
  NOT EXISTS (
    SELECT
      *
    FROM
      permission
    WHERE
      permissions_id = currval('hibernate_sequence') AND
      permissions = 'DELETE'
  );
