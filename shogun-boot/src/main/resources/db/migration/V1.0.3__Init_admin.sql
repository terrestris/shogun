SET search_path TO shogun;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    null,
    null,
    'admin',
    true,
    crypt('shogun', gen_salt('bf')),
    'admin'
) ON CONFLICT DO NOTHING;

INSERT INTO identities VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    null,
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
) ON CONFLICT DO NOTHING;

INSERT INTO userinstancepermissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM permissions WHERE name = 'ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
) ON CONFLICT DO NOTHING;

INSERT INTO userclasspermissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'de.terrestris.shoguncore.model.Application',
    (SELECT id FROM permissions WHERE name = 'ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
) ON CONFLICT DO NOTHING;

INSERT INTO userclasspermissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'de.terrestris.shoguncore.model.User',
    (SELECT id FROM permissions WHERE name = 'ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
);

INSERT INTO userclasspermissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'de.terrestris.shoguncore.model.Identity',
    (SELECT id FROM permissions WHERE name = 'ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
);

INSERT INTO userclasspermissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'de.terrestris.shoguncore.model.Group',
    (SELECT id FROM permissions WHERE name = 'ADMIN'),
    (SELECT id FROM users WHERE username = 'admin')
);
