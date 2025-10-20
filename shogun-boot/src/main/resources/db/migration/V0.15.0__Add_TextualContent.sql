CREATE TABLE IF NOT EXISTS shogun.textualcontents (
	id bigint PRIMARY KEY,
	created timestamp without time zone NULL,
	modified timestamp without time zone NULL,
	markdown text NOT NULL,
	title text NOT NULL,
	category text NOT NULL
);
