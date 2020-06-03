package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupClassPermissionRepository extends BaseCrudRepository<GroupClassPermission, Long>, JpaSpecificationExecutor<GroupClassPermission> {

    @Query("Select gcp from groupclasspermissions gcp where gcp.group.id = ?1 and gcp.className = ?2")
    Optional<GroupClassPermission> findByGroupIdAndClassName(Long groupId, String className);

}
