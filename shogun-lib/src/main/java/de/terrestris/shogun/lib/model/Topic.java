package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.terrestris.shogun.lib.resolver.ImageFileIdResolver;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
@Audited
@AuditTable(value = "topics_rev", schema = "shogun_rev")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Topic extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @OneToOne
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        resolver = ImageFileIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId = true)
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
