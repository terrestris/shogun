SET search_path TO shogun;

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'ADMIN'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'ADMIN'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'UPDATE'
);

INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ_UPDATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ_DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_UPDATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_UPDATE_DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ_UPDATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ_DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'UPDATE_DELETE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);
INSERT INTO permission VALUES (
    (SELECT id FROM permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);
