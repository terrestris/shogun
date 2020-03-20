INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'ADMIN'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'ADMIN'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'UPDATE'
);

INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ_UPDATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_READ_DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_UPDATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_UPDATE_DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'CREATE_DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'CREATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ_UPDATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'READ_DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'READ'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);

INSERT INTO shogun.permissions VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    'UPDATE_DELETE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'UPDATE'
);
INSERT INTO shogun.permission VALUES (
    (SELECT id FROM shogun.permissions WHERE id = currval('hibernate_sequence')),
    'DELETE'
);
