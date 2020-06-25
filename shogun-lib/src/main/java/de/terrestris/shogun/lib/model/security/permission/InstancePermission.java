package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@MappedSuperclass
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class InstancePermission extends BaseEntity {

    @Column
    private Long entityId;

    @OneToOne(optional = false)
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = NOT_AUDITED)
    private PermissionCollection permissions;

}
