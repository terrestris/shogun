package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "permissions")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionCollection extends BaseEntity {

    @ElementCollection
    @CollectionTable(name="permission", schema = "shogun")
    @Enumerated(EnumType.STRING)
    @Fetch(FetchMode.JOIN)
    private Set<PermissionType> permissions = new HashSet<>();

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionCollectionType name;

}
