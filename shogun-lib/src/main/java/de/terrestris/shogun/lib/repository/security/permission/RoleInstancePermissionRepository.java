/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.security.permission.RoleInstancePermission;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleInstancePermissionRepository extends BasePermissionRepository<RoleInstancePermission, Long>,
    JpaSpecificationExecutor<RoleInstancePermission> {

    @Query("Select rip from roleinstancepermissions rip where rip.role.id = ?1 and rip.entityId = ?2")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<RoleInstancePermission> findByRoleIdAndEntityId(Long roleId, Long entityId);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<RoleInstancePermission> findByEntityId(Long entityId);

    @Query("SELECT rip FROM roleinstancepermissions rip LEFT JOIN rip.permission p WHERE rip.entityId = :entityId AND p.name = :permissionCollectionType")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<RoleInstancePermission> findByEntityAndPermissionCollectionType(
        @Param("entityId") Long entityId,
        @Param("permissionCollectionType") PermissionCollectionType permissionCollectionType
    );

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<RoleInstancePermission> findAllByRole(Role role);
}
