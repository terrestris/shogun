INSERT INTO roles VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'The user role',
    'ROLE_USER'
) ON CONFLICT DO NOTHING;

INSERT INTO roles VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'The admin role',
    'ROLE_ADMIN'
) ON CONFLICT DO NOTHING;
