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

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupClassPermissionRepository extends BasePermissionRepository<GroupClassPermission, Long>,
    JpaSpecificationExecutor<GroupClassPermission> {

    @Query("Select gcp from groupclasspermissions gcp where gcp.group.id = ?1 and gcp.className = ?2")
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<GroupClassPermission> findByGroupIdAndClassName(Long groupId, String className);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    List<GroupClassPermission> findAllByGroup(Group group);

    List<GroupClassPermission> findByClassName(String className);
}
