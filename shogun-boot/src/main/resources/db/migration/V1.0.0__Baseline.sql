CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;

SET search_path TO shogun;

CREATE SEQUENCE hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE applications (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    i18n jsonb,
    layer_config jsonb,
    layer_tree jsonb,
    name text,
    state_only boolean,
    tool_config jsonb,
    CONSTRAINT applications_pkey PRIMARY KEY (id),
    CONSTRAINT applications_unique_id UNIQUE (id)
);

CREATE TABLE files (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    active boolean,
    file bytea,
    file_name text NOT NULL,
    file_type text NOT NULL,
    file_uuid uuid NOT NULL,
    CONSTRAINT files_pkey PRIMARY KEY (id),
    CONSTRAINT files_unique_id UNIQUE (id)
);

CREATE TABLE users (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL,
    client_config jsonb,
    details jsonb,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_unique_id UNIQUE (id)
);

CREATE TABLE groups (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL,
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT groups_unique_id UNIQUE (id)
);

CREATE TABLE permissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name text NOT NULL,
    CONSTRAINT permissions_pkey PRIMARY KEY (id),
    CONSTRAINT permissions_unique_id UNIQUE (id),
    CONSTRAINT permissions_unique_name UNIQUE (name)
);

CREATE TABLE groupclasspermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL,
    group_id bigint NOT NULL,
    CONSTRAINT groupclasspermissions_pkey PRIMARY KEY (id),
    -- CONSTRAINT groupclasspermissions_unique_permissions_id UNIQUE (permissions_id),
    CONSTRAINT groupclasspermissions_unique_id UNIQUE (id),
    CONSTRAINT groupclasspermissions_fkey_group_id FOREIGN KEY (group_id)
        REFERENCES groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT groupclasspermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE groupinstancepermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL,
    group_id bigint NOT NULL,
    CONSTRAINT groupinstancepermissions_pkey PRIMARY KEY (id),
    CONSTRAINT groupinstancepermissions_unique_id UNIQUE (id),
    -- CONSTRAINT groupinstancepermissions_unique_permissions_id UNIQUE (permissions_id),
    CONSTRAINT groupinstancepermissions_fkey_group_id FOREIGN KEY (group_id)
        REFERENCES groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT groupinstancepermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);


CREATE TABLE imagefiles (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    active boolean,
    file bytea,
    file_name text NOT NULL,
    file_type text NOT NULL,
    file_uuid uuid NOT NULL,
    height integer NOT NULL,
    thumbnail bytea,
    width integer NOT NULL,
    CONSTRAINT imagefiles_pkey PRIMARY KEY (id),
    CONSTRAINT imagefiles_unique_id UNIQUE (id)
);

CREATE TABLE layers (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    features jsonb,
    name text NOT NULL,
    source_config jsonb NOT NULL,
    type text NOT NULL,
    CONSTRAINT layers_pkey PRIMARY KEY (id),
    CONSTRAINT layers_unique_id UNIQUE (id),
    CONSTRAINT layers_unique_name UNIQUE (name)
);

CREATE TABLE permission (
    permissions_id bigint NOT NULL,
    permissions text,
    CONSTRAINT permission_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE userclasspermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT userclasspermissions_pkey PRIMARY KEY (id),
    -- CONSTRAINT userclasspermissions_unique_permissions_id UNIQUE (permissions_id),
    CONSTRAINT userclasspermissions_unique_id UNIQUE (id),
    CONSTRAINT userclasspermissions_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT userclasspermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE userinstancepermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT userinstancepermissions_pkey PRIMARY KEY (id),
    -- CONSTRAINT userinstancepermissions_unique_permissions_id UNIQUE (permissions_id),
    CONSTRAINT userinstancepermissions_unique_id UNIQUE (id),
    CONSTRAINT userinstancepermissions_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT userinstancepermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);
