CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;

SET search_path TO shogun, public;

CREATE SEQUENCE hibernate_sequence
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE verification_token_id_seq
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

CREATE TABLE entityoperation (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name text NOT NULL,
    CONSTRAINT entityoperation_pkey PRIMARY KEY (id),
    CONSTRAINT entityoperation_unique_id UNIQUE (id)
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

CREATE TABLE roles (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    description text,
    name text NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_unique_id UNIQUE (id),
    CONSTRAINT roles_unique_name UNIQUE (name)
);

CREATE TABLE users (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    client_config jsonb,
    details jsonb,
    email text NOT NULL,
    enabled boolean,
    password text,
    username text NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_unique_email UNIQUE (email),
    CONSTRAINT users_unique_id UNIQUE (id),
    CONSTRAINT users_unique_username UNIQUE (username)
);

CREATE TABLE groups (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name text NOT NULL,
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT groups_unique_id UNIQUE (id),
    CONSTRAINT groups_unique_name UNIQUE (name)
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

CREATE TABLE identities (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    group_id bigint,
    role_id bigint,
    user_id bigint,
    CONSTRAINT identities_pkey PRIMARY KEY (id),
    CONSTRAINT identities_unique_id UNIQUE (id),
    CONSTRAINT identities_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT identities_fkey_group_id FOREIGN KEY (group_id)
        REFERENCES groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT identities_fkey_role_id FOREIGN KEY (role_id)
        REFERENCES roles (id) MATCH SIMPLE
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

CREATE TABLE serviceaccess (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    event text NOT NULL,
    operation text NOT NULL,
    rule text NOT NULL,
    service text NOT NULL,
    CONSTRAINT serviceaccess_pkey PRIMARY KEY (id),
    CONSTRAINT serviceaccess_unique_id UNIQUE (id)
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

CREATE TABLE userverificationtokens (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    expiry_date timestamp without time zone NOT NULL,
    token text NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT userverificationtokens_pkey PRIMARY KEY (id),
    CONSTRAINT userverificationtokens_fkey_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

