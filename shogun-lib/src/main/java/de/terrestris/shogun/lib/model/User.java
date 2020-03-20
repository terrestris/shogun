package de.terrestris.shogun.lib.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "users")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String keycloakId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> details = new HashMap<>();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> clientConfig = new HashMap<>();

}

