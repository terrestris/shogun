package de.terrestris.shogun.lib.model;

import lombok.*;
import org.keycloak.representations.idm.GroupRepresentation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "groups")
@Table(schema = "shogun")
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
