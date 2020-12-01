package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class InstancePermission extends BaseEntity {

    @ManyToOne(optional = false)
    private BaseEntity entity;

    @OneToOne(optional = false)
    @Fetch(FetchMode.JOIN)
    private PermissionCollection permissions;

}
