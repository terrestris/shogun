package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionCollection extends BaseEntity {

    @ElementCollection()
    @CollectionTable(name="permission")
    @Enumerated(EnumType.STRING)
    @Fetch(FetchMode.JOIN)
    private Set<PermissionType> permissions = new HashSet<>();

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionCollectionType name;

    public PermissionCollection (Set<PermissionType> permissions) {
        super();
        this.permissions = permissions;
    }

}
