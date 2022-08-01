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
import de.terrestris.shogun.lib.service.security.permission.internal.UserInstancePermissionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserInstancePermissionServiceSecured extends UserInstancePermissionService {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#user, 'READ')")
    public List<UserInstancePermission> findFor(User user) {
        return super.findFor(user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
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
    // todo: filter list of users (postfilter?)
    public List<User> findOwner(BaseEntity entity) {
        return super.findOwner(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        return super.findPermissionCollectionFor(entity, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#persistedEntity, 'UPDATE')")
    public void setPermission(BaseEntity persistedEntity, User user, PermissionCollectionType permissionCollectionType) {
        super.setPermission(persistedEntity, user, permissionCollectionType);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // todo: permission check!
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

    // basePermissionService methods
    // todo: add permissions for non-admins

    @Override
    @PostFilter("hasRole('ROLE_ADMIN')")
    public List<UserInstancePermission> findAll() {
        return super.findAll();
    }

    @Override
    @PostFilter("hasRole('ROLE_ADMIN')")
    public List<UserInstancePermission> findAllBy(Specification specification) {
        return super.findAllBy(specification);
    }

    @Override
    @PostAuthorize("hasRole('ROLE_ADMIN')")
    public Optional<UserInstancePermission> findOne(Long id) {
        return super.findOne(id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserInstancePermission create(UserInstancePermission entity) {
        return super.create(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserInstancePermission update(Long id, UserInstancePermission entity) throws IOException {
        return super.update(id, entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(UserInstancePermission entity) {
        super.delete(entity);
    }
}
