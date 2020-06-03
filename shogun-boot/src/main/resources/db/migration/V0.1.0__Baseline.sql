CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;

CREATE SCHEMA IF NOT EXISTS shogun;
SET search_path TO shogun, public;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS applications (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    i18n jsonb,
    layer_config jsonb,
    layer_tree jsonb,
    name text,
    state_only boolean,
    tool_config jsonb
);

CREATE TABLE IF NOT EXISTS files (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    active boolean,
    file bytea,
    file_name text NOT NULL,
    file_type text NOT NULL,
    file_uuid uuid NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL,
    client_config jsonb,
    details jsonb
);

CREATE TABLE IF NOT EXISTS groups (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL
);

CREATE TABLE IF NOT EXISTS permissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    name text NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS groupclasspermissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL REFERENCES permissions (id),
    group_id bigint NOT NULL REFERENCES groups (id)
);

CREATE TABLE IF NOT EXISTS groupinstancepermissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL REFERENCES permissions (id),
    group_id bigint NOT NULL REFERENCES groups (id)
);

CREATE TABLE IF NOT EXISTS imagefiles (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    active boolean,
    file bytea,
    file_name text NOT NULL,
    file_type text NOT NULL,
    file_uuid uuid NOT NULL,
    height integer NOT NULL,
    thumbnail bytea,
    width integer NOT NULL
);

CREATE TABLE IF NOT EXISTS layers (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    features jsonb,
    name text NOT NULL,
    source_config jsonb NOT NULL,
    type text NOT NULL
);

CREATE TABLE IF NOT EXISTS permission (
    permissions_id bigint NOT NULL REFERENCES permissions (id),
    permissions text
);

CREATE TABLE IF NOT EXISTS userclasspermissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL REFERENCES permissions (id),
    user_id bigint NOT NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS userinstancepermissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL REFERENCES permissions (id),
    user_id bigint NOT NULL REFERENCES users (id)
);
