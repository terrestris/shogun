CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;

CREATE SCHEMA IF NOT EXISTS shogun;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE shogun.applications (
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

CREATE TABLE shogun.files (
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

CREATE TABLE shogun.users (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL,
    client_config jsonb,
    details jsonb,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_unique_id UNIQUE (id)
);

CREATE TABLE shogun.groups (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    keycloak_id text NOT NULL,
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT groups_unique_id UNIQUE (id)
);

CREATE TABLE shogun.permissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name text NOT NULL,
    CONSTRAINT permissions_pkey PRIMARY KEY (id),
    CONSTRAINT permissions_unique_id UNIQUE (id),
    CONSTRAINT permissions_unique_name UNIQUE (name)
);

CREATE TABLE shogun.groupclasspermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL,
    group_id bigint NOT NULL,
    CONSTRAINT groupclasspermissions_pkey PRIMARY KEY (id),
    CONSTRAINT groupclasspermissions_unique_id UNIQUE (id),
    CONSTRAINT groupclasspermissions_fkey_group_id FOREIGN KEY (group_id)
        REFERENCES shogun.groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT groupclasspermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES shogun.permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE shogun.groupinstancepermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL,
    group_id bigint NOT NULL,
    CONSTRAINT groupinstancepermissions_pkey PRIMARY KEY (id),
    CONSTRAINT groupinstancepermissions_unique_id UNIQUE (id),
    CONSTRAINT groupinstancepermissions_fkey_group_id FOREIGN KEY (group_id)
        REFERENCES shogun.groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT groupinstancepermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES shogun.permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE shogun.imagefiles (
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

CREATE TABLE shogun.layers (
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

CREATE TABLE shogun.permission (
    permissions_id bigint NOT NULL,
    permissions text,
    CONSTRAINT permission_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES shogun.permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE shogun.userclasspermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    class_name text,
    permissions_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT userclasspermissions_pkey PRIMARY KEY (id),
    CONSTRAINT userclasspermissions_unique_id UNIQUE (id),
    CONSTRAINT userclasspermissions_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES shogun.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT userclasspermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES shogun.permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE shogun.userinstancepermissions (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    entity_id bigint NOT NULL,
    permissions_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT userinstancepermissions_pkey PRIMARY KEY (id),
    CONSTRAINT userinstancepermissions_unique_id UNIQUE (id),
    CONSTRAINT userinstancepermissions_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES shogun.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT userinstancepermissions_fkey_permissions_id FOREIGN KEY (permissions_id)
        REFERENCES shogun.permissions (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);
