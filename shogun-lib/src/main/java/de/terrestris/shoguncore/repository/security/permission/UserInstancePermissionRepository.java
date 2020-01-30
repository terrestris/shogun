package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.security.permission.UserInstancePermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInstancePermissionRepository extends BaseCrudRepository<UserInstancePermission, Long>, JpaSpecificationExecutor<UserInstancePermission> {
}
