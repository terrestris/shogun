package de.terrestris.shogun.lib.model;

import de.terrestris.shogun.lib.enumeration.LayerType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;

@Entity(name = "layers")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Layer extends BaseEntity {

    @Column(unique = true, nullable = false)
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
