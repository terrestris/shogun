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

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
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
public interface UserClassPermissionRepository extends BasePermissionRepository<UserClassPermission, Long>,
    JpaSpecificationExecutor<UserClassPermission> {

    @Query("Select ucp from userclasspermissions ucp where ucp.user.id = ?1 and ucp.className = ?2")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<UserClassPermission> findByUserIdAndClassName(Long userId, String className);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<UserClassPermission> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM {h-schema}userclasspermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

    List<UserClassPermission> findByClassName(String className);
}
