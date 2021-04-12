package de.terrestris.shogun.lib.model;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.keycloak.representations.idm.GroupRepresentation;

import javax.persistence.*;

@Entity(name = "groups")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "groups_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String keycloakId;

    @Transient
    private GroupRepresentation keycloakRepresentation;

}
