package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.security.permission.GroupInstancePermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInstancePermissionRepository extends BaseCrudRepository<GroupInstancePermission, Long>, JpaSpecificationExecutor<GroupInstancePermission> {
}
