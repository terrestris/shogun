SET search_path TO shogun, public;

CREATE TABLE topics (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    title text not null,
    description text,
    layerTree jsonb,
    searchLayerConfigs jsonb,
    img_src_id bigint,
    CONSTRAINT topics_pkey PRIMARY KEY (id),
    CONSTRAINT topics_unique_id UNIQUE (id),
    CONSTRAINT topics_fkey_img_src_id FOREIGN KEY (img_src_id)
        REFERENCES imagefiles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);
