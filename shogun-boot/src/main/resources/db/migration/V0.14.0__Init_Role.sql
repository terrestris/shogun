CREATE TABLE IF NOT EXISTS shogun.roles (
    id BIGINT PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    auth_provider_id TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS shogun.roleclasspermissions (
    id BIGINT PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    class_name TEXT,
    permission_id BIGINT NOT NULL REFERENCES permissions (id),
    role_id BIGINT NOT NULL REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS shogun.roleinstancepermissions (
    id BIGINT PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    entity_id bigint NOT NULL,
    permission_id bigint NOT NULL REFERENCES permissions (id),
    role_id BIGINT NOT NULL REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS shogun_rev.roles_rev (
    id BIGINT,
    rev INTEGER REFERENCES shogun_rev.revinfo (rev),
    revtype SMALLINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    auth_provider_id TEXT,
    created_mod BOOLEAN,
    modified_mod BOOLEAN,
    auth_provider_id_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.roleclasspermissions_rev (
    id BIGINT,
    rev INTEGER REFERENCES shogun_rev.revinfo (rev),
    revtype SMALLINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    class_name TEXT,
    permission_id BIGINT,
    role_id BIGINT,
    created_mod BOOLEAN,
    modified_mod BOOLEAN,
    class_name_mod BOOLEAN,
    permission_id_mod BOOLEAN,
    permission_mod BOOLEAN,
    role_id_mod BOOLEAN,
    role_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.roleinstancepermissions_rev (
    id BIGINT,
    rev INTEGER REFERENCES shogun_rev.revinfo (rev),
    revtype SMALLINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    modified TIMESTAMP WITHOUT TIME ZONE,
    entity_id BIGINT,
    permission_id BIGINT,
    role_id bigint,
    created_mod BOOLEAN,
    modified_mod BOOLEAN,
    entity_id_mod BOOLEAN,
    permission_id_mod BOOLEAN,
    permission_mod BOOLEAN,
    role_id_mod BOOLEAN,
    role_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);