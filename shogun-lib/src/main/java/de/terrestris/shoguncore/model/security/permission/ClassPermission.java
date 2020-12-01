package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import javax.persistence.Column;
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
public abstract class ClassPermission extends BaseEntity {

    @Column
    private String className;

    @OneToOne(optional = false)
    @Fetch(FetchMode.JOIN)
    private PermissionCollection permissions;

}
