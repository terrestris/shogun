-- shogun.textualcontents definition

-- Drop table

-- DROP TABLE shogun.textualcontents;

CREATE TABLE shogun.textualcontents (
	id int8 NOT NULL,
	created timestamp NULL,
	modified timestamp NULL,
	markdown text NOT NULL,
	title text NOT NULL,
	category text NOT NULL,
	CONSTRAINT textualcontents_pkey_1 PRIMARY KEY (id)
);