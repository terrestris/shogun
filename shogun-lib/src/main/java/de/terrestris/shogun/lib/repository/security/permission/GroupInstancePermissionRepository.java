package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInstancePermissionRepository extends BaseCrudRepository<GroupInstancePermission, Long>, JpaSpecificationExecutor<GroupInstancePermission> {

    @Query("Select gip from groupinstancepermissions gip where gip.group.id = ?1 and gip.entityId = ?2")
    Optional<GroupInstancePermission> findByGroupIdAndEntityId(Long groupId, Long entityId);

    List<GroupInstancePermission> findByEntityId(Long entityId);

}
