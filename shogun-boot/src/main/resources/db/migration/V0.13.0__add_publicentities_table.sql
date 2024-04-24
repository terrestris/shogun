CREATE TABLE IF NOT EXISTS shogun.publicentities (
    id bigint PRIMARY KEY,
    created timestamp without time zone,
    modified timestamp without time zone,
	entity_id int8 NOT NULL UNIQUE
);
