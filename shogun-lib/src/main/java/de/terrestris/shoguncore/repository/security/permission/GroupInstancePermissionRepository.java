package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.security.permission.GroupInstancePermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupInstancePermissionRepository extends BaseCrudRepository<GroupInstancePermission, Long>, JpaSpecificationExecutor<GroupInstancePermission> {

    List<GroupInstancePermission> findAllByEntityId(Long entityId);

}
