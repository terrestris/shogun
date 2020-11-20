package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.terrestris.shogun.lib.resolver.ImageFileIdResolver;
import java.util.Map;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "topics")
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
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    @ToString.Exclude
    private Map<String, Object> layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "searchlayerconfigs")
    @Basic(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ToString.Exclude
    private Set<Map<String, Object>> searchLayerConfigs;

}
