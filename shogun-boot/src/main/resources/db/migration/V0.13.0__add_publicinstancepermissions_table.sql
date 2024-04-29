CREATE TABLE IF NOT EXISTS shogun.publicinstancepermissions (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
	entity_id int8 NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS shogun_rev.publicinstancepermissions_rev (
    id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
    created timestamp without time zone,
    modified timestamp without time zone,
	entity_id int8,
    PRIMARY KEY (id, rev)
);