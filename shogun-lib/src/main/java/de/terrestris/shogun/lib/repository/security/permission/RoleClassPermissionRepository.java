/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.security.permission.RoleClassPermission;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleClassPermissionRepository extends BasePermissionRepository<RoleClassPermission, Long>,
    JpaSpecificationExecutor<RoleClassPermission> {

    @Query("Select rcp from roleclasspermissions rcp where rcp.role.id = ?1 and rcp.className = ?2")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<RoleClassPermission> findByRoleIdAndClassName(Long roleId, String className);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<RoleClassPermission> findAllByRole(Role role);

    @Modifying
    @Query(value = "DELETE FROM {h-schema}roleclasspermissions u WHERE u.role_id=:roleId", nativeQuery = true)
    void deleteAllByRoleId(@Param("roleId") Long roleId);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<RoleClassPermission> findByClassName(String className);
}
