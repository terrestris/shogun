package de.terrestris.shogun.lib.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "groups")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String keycloakId;

}
