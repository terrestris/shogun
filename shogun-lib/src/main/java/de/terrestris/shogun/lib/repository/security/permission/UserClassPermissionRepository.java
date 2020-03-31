package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserClassPermissionRepository extends BaseCrudRepository<UserClassPermission, Long>, JpaSpecificationExecutor<UserClassPermission> {

    @Query("Select ucp from userclasspermissions ucp where ucp.user.id = ?1 and ucp.className = ?2")
    Optional<UserClassPermission> findByUserIdAndClassName(Long userId, String className);

}
