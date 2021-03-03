package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
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
public interface UserInstancePermissionRepository extends BasePermissionRepository<UserInstancePermission, Long>,
    JpaSpecificationExecutor<UserInstancePermission> {

    @Query("Select uip from userinstancepermissions uip where uip.user.id = ?1 and uip.entityId = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<UserInstancePermission> findByUserIdAndEntityId(Long userId, Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityId(Long entityId);

    @Query("SELECT u FROM userinstancepermissions u LEFT JOIN u.permissions p WHERE u.entityId = :entityId AND p.name = :permissionCollectionType")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityAndPermissionCollectionType(
        @Param("entityId") Long entityId,
        @Param("permissionCollectionType") PermissionCollectionType permissionCollectionType
    );

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserInstancePermission> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM userinstancepermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

}
