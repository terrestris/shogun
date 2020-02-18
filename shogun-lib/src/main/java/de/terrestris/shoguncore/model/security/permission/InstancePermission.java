package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class InstancePermission extends BaseEntity {

    @ManyToOne(optional = false)
    private BaseEntity entity;

    @OneToOne(optional = false)
    @Fetch(FetchMode.JOIN)
    private PermissionCollection permissions;

}
