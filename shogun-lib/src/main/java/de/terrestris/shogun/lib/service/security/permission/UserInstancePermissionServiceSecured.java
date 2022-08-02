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
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserInstancePermissionServiceSecured extends UserInstancePermissionService {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#user, 'READ')")
//    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    // todo: postfilter permission check: how to get either targetDomainType or entity from permission.entityId?
    public List<UserInstancePermission> findFor(User user) {
        return super.findFor(user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#user, 'READ'))")
    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {
        return super.findFor(entity, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public List<UserInstancePermission> findFor(BaseEntity entity) {
        return super.findFor(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public List<UserInstancePermission> findFor(BaseEntity entity, PermissionCollectionType permissionCollectionType) {
        return super.findFor(entity, permissionCollectionType);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    public List<User> findOwner(BaseEntity entity) {
        return super.findOwner(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#entity, 'READ') and hasPermission(#user, 'READ'))")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        return super.findPermissionCollectionFor(entity, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasPermission(#persistedEntity, 'UPDATE') and hasPermission(#user, 'READ'))")
    public void setPermission(BaseEntity persistedEntity, User user, PermissionCollectionType permissionCollectionType) {
        super.setPermission(persistedEntity, user, permissionCollectionType);
    }

    @Override
    @PreFilter(filterTarget = "persistedEntityList", value = "hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'UPDATE')")
    public void setPermission(List<? extends BaseEntity> persistedEntityList, User user, PermissionCollectionType permissionCollectionType) {
        super.setPermission(persistedEntityList, user, permissionCollectionType);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#persistedEntity, 'DELETE')")
    public void deleteAllFor(BaseEntity persistedEntity) {
        super.deleteAllFor(persistedEntity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#persistedEntity, 'DELETE')")
    public void deleteFor(BaseEntity persistedEntity, User user) {
        super.deleteFor(persistedEntity, user);
    }

}
