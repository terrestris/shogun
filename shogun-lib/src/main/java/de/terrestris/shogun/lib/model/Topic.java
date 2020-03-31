package de.terrestris.shogun.lib.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

/**
 Entity representing a topic configuration, in particular:
 * name, description, ...
 * layer tree
 * list of search layer configurations
 */
@Entity(name = "topics")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Topic extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String title;

    @Column(columnDefinition="text")
    private String description;

    @OneToOne
    @Fetch(FetchMode.JOIN)
    private ImageFile imgSrc;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "layertree")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "searchlayerconfigs")
    @Basic(fetch = FetchType.LAZY)
    private Set<Map<String, Object>> searchLayerConfigs;

}
