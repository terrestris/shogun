package de.terrestris.shogun.lib.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.keycloak.representations.idm.UserRepresentation;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "users")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "users_rev", schema = "shogun_rev")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String keycloakId;

    @Transient
    private UserRepresentation keycloakRepresentation;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> details = new HashMap<>();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> clientConfig = new HashMap<>();

}

