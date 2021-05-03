ALTER TABLE shogun.files ADD COLUMN IF NOT EXISTS path text;
ALTER TABLE shogun.imagefiles ADD COLUMN IF NOT EXISTS path text;
