package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserClassPermissionRepository extends BasePermissionRepository<UserClassPermission, Long>,
    JpaSpecificationExecutor<UserClassPermission> {

    @Query("Select ucp from userclasspermissions ucp where ucp.user.id = ?1 and ucp.className = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<UserClassPermission> findByUserIdAndClassName(Long userId, String className);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserClassPermission> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM userclasspermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);
}
