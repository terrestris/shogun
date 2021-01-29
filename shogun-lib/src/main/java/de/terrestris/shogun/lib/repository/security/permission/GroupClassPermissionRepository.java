package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupClassPermissionRepository extends BaseCrudRepository<GroupClassPermission, Long>, JpaSpecificationExecutor<GroupClassPermission> {

    @Query("Select gcp from groupclasspermissions gcp where gcp.group.id = ?1 and gcp.className = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<GroupClassPermission> findByGroupIdAndClassName(Long groupId, String className);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<GroupClassPermission> findAllByGroup(Group group);
}
