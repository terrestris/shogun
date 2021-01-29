package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInstancePermissionRepository extends BaseCrudRepository<GroupInstancePermission, Long>, JpaSpecificationExecutor<GroupInstancePermission> {

    @Query("Select gip from groupinstancepermissions gip where gip.group.id = ?1 and gip.entityId = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<GroupInstancePermission> findByGroupIdAndEntityId(Long groupId, Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<GroupInstancePermission> findByEntityId(Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<GroupInstancePermission> findAllByGroup(Group group);

}
