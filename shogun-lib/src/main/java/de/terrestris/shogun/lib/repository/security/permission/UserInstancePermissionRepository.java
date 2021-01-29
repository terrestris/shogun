package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
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
public interface UserInstancePermissionRepository extends BaseCrudRepository<UserInstancePermission, Long>, JpaSpecificationExecutor<UserInstancePermission> {

    @Query("Select uip from userinstancepermissions uip where uip.user.id = ?1 and uip.entityId = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<UserInstancePermission> findByUserIdAndEntityId(Long userId, Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityId(Long entityId);

    @Query("SELECT u FROM userinstancepermissions u LEFT JOIN u.permissions p WHERE u.entity = :entity AND p.name = :permissionCollectionType")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityAndPermissionCollectionType(
        @Param("entity") BaseEntity entity,
        @Param("permissionCollectionType") PermissionCollectionType permissionCollectionType
    );

    @Modifying
    @Query(value = "DELETE FROM userinstancepermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

}
