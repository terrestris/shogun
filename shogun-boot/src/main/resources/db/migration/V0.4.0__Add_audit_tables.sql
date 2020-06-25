CREATE SCHEMA IF NOT EXISTS shogun_rev;

SET search_path TO shogun_rev, public;

CREATE TABLE IF NOT EXISTS shogun_rev.revinfo (
    rev bigint PRIMARY KEY,
    revtstmp bigint
);

CREATE TABLE IF NOT EXISTS shogun_rev.applications_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    i18n jsonb,
    layer_config jsonb,
    layer_tree jsonb,
    name text,
    state_only boolean,
    tool_config jsonb,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.groupclasspermissions_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint,
    group_id bigint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.groupinstancepermissions_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint,
    permissions_id bigint,
    group_id bigint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.groups_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.layers_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    features jsonb,
    name text,
    source_config jsonb,
    type text,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.topics_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    description text COLLATE pg_catalog."default",
    layertree jsonb,
    searchlayerconfigs jsonb,
    title text,
    img_src_id bigint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.userclasspermissions_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint,
    user_id bigint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.userinstancepermissions_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint,
    permissions_id bigint,
    user_id bigint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS shogun_rev.users_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    details jsonb,
    keycloak_id text,
    PRIMARY KEY (id, rev)
);