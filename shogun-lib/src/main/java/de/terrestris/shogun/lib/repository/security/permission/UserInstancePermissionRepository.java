package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInstancePermissionRepository extends BaseCrudRepository<UserInstancePermission, Long>, JpaSpecificationExecutor<UserInstancePermission> {

    @Query("Select uip from userinstancepermissions uip where uip.user.id = ?1 and uip.entityId = ?2")
    Optional<UserInstancePermission> findByUserIdAndEntityId(Long userId, Long entityId);

    List<UserInstancePermission> findByEntityId(Long entityId);

}
