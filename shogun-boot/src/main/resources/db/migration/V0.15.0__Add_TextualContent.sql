CREATE TABLE IF NOT EXISTS shogun.textualcontents (
	id bigint PRIMARY KEY,
	created timestamp without time zone NULL,
	modified timestamp without time zone NULL,
	markdown text NOT NULL,
	title text NOT NULL,
	category text NOT NULL
);

CREATE TABLE IF NOT EXISTS shogun_rev.textualcontents_rev (
	id bigint,
    rev integer REFERENCES shogun_rev.revinfo (rev),
    revtype smallint,
	created timestamp without time zone NULL,
	modified timestamp without time zone NULL,
	markdown text NULL,
	title text NULL,
	category text NULL,
	PRIMARY KEY (id, rev)
);

alter table shogun_rev.textualcontents_rev add column if not exists created_mod bool;
alter table shogun_rev.textualcontents_rev add column if not exists modified_mod bool;
alter table shogun_rev.textualcontents_rev add column if not exists markdown_mod bool;
alter table shogun_rev.textualcontents_rev add column if not exists title_mod bool;
alter table shogun_rev.textualcontents_rev add column if not exists category_mod bool;
