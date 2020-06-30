package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.FetchType;
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

    /**
     * Attention: Hibernate will create an unwanted UNIQUE constraint for the join column <i>permissions_id</i> if
     *            in create mode. To prevent unique constraint violations the constraint has to be removed manually.
     */
    @OneToOne(
        optional = false,
        fetch = FetchType.LAZY
    )
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = NOT_AUDITED)
    private PermissionCollection permissions;

}
