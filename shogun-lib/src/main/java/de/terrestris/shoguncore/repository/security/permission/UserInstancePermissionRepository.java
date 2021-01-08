package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.security.permission.UserInstancePermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInstancePermissionRepository extends BaseCrudRepository<UserInstancePermission, Long>, JpaSpecificationExecutor<UserInstancePermission> {

    List<UserInstancePermission> findAllByEntityId(Long entityId);

    @Query("SELECT u FROM userinstancepermissions u LEFT JOIN u.permissions p WHERE u.entity = :entity AND p.name = :permissionCollectionType")
    List<UserInstancePermission> findByEntityAndPermissionCollectionType(
        @Param("entity") BaseEntity entity,
        @Param("permissionCollectionType") PermissionCollectionType permissionCollectionType
    );

}
