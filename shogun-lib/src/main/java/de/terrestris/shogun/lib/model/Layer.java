package de.terrestris.shogun.lib.model;

import de.terrestris.shogun.lib.enumeration.LayerType;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity(name = "layers")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "layers_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "layers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Layer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> sourceConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> features;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LayerType type;
}
