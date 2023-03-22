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
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserInstancePermissionRepository extends BasePermissionRepository<UserInstancePermission, Long>,
    JpaSpecificationExecutor<UserInstancePermission> {

    @Query("Select uip from userinstancepermissions uip where uip.user.id = ?1 and uip.entityId = ?2")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<UserInstancePermission> findByUserIdAndEntityId(Long userId, Long entityId);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityId(Long entityId);

    @Query("SELECT u FROM userinstancepermissions u LEFT JOIN u.permission p WHERE u.entityId = :entityId AND p.name = :permissionCollectionType")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<UserInstancePermission> findByEntityAndPermissionCollectionType(
        @Param("entityId") Long entityId,
        @Param("permissionCollectionType") PermissionCollectionType permissionCollectionType
    );

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<UserInstancePermission> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM {h-schema}userinstancepermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

}
