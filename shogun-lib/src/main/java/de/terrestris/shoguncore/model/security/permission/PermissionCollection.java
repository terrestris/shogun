package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
