SET search_path TO shogun, public;

ALTER TABLE imagefiles ALTER COLUMN height DROP NOT NULL;
ALTER TABLE imagefiles ALTER COLUMN width DROP NOT NULL;
