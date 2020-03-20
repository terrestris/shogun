CREATE SCHEMA IF NOT EXISTS interceptor;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE interceptor.interceptor_rule (
    id bigint NOT NULL PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    end_point text,
    event text NOT NULL,
    operation text,
    rule text NOT NULL,
    service text NOT NULL
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'REQUEST',
    NULL,
    'ALLOW',
    'WMS'
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'REQUEST',
    NULL,
    'ALLOW',
    'WFS'
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'REQUEST',
    NULL,
    'ALLOW',
    'WCS'
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'RESPONSE',
    NULL,
    'ALLOW',
    'WMS'
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'RESPONSE',
    NULL,
    'ALLOW',
    'WFS'
);

INSERT INTO interceptor.interceptor_rule VALUES (
    nextval('hibernate_sequence'),
    NOW()::timestamp,
    NOW()::timestamp,
    NULL,
    'RESPONSE',
    NULL,
    'ALLOW',
    'WCS'
);
