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
package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class GroupClassPermissionServiceSecured extends GroupClassPermissionService {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public List<GroupClassPermission> findFor(BaseEntity entity) {
        return super.findFor(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#group, 'READ')")
    //    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    // todo: in postfilter permission check: how to get either targetDomainType or entity from permission.entityId?
    public List<GroupClassPermission> findFor(Group group) {
        return super.findFor(group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#group, 'READ'))")
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group) {
        return super.findFor(entity, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#clazz, 'READ') and hasPermission(#group, 'READ'))")
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, Group group) {
        return super.findFor(clazz, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#clazz, 'READ') and hasPermission(#user, 'READ'))")
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        return super.findFor(clazz, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#group, 'READ') and hasPermission(#user, 'READ'))")
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group, User user) {
        return super.findFor(entity, group, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#group, 'READ'))")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        return super.findPermissionCollectionFor(entity, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#user, 'READ'))")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        return super.findPermissionCollectionFor(entity, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#group, 'READ') and hasPermission(#user, 'READ'))")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group, User user) {
        return super.findPermissionCollectionFor(entity, group, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#clazz, 'UPDATE') and hasPermission(#group, 'READ'))")
    // todo: test class permission check
    public void setPermission(Class<? extends BaseEntity> clazz, Group group, PermissionCollectionType permissionCollectionType) {
        super.setPermission(clazz, group, permissionCollectionType);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#persistedEntity, 'DELETE')")
    public void deleteAllFor(BaseEntity persistedEntity) {
        super.deleteAllFor(persistedEntity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#persistedEntity, 'DELETE')")
    public void deleteFor(BaseEntity persistedEntity, Group group) {
        super.deleteFor(persistedEntity, group);
    }

}
