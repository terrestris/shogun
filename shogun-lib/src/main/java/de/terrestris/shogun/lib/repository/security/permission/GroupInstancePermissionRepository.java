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
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInstancePermissionRepository extends BaseCrudRepository<GroupInstancePermission, Long>, JpaSpecificationExecutor<GroupInstancePermission> {

    @Query("Select gip from groupinstancepermissions gip where gip.group.id = ?1 and gip.entityId = ?2")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<GroupInstancePermission> findByGroupIdAndEntityId(Long groupId, Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<GroupInstancePermission> findByEntityId(Long entityId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<GroupInstancePermission> findAllByGroup(Group group);

}
