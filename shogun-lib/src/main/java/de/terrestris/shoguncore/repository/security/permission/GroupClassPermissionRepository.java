package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.security.permission.GroupClassPermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupClassPermissionRepository extends BaseCrudRepository<GroupClassPermission, Long>, JpaSpecificationExecutor<GroupClassPermission> {

    List<GroupClassPermission> findAllByGroup(Group group);

}
