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
import de.terrestris.shogun.lib.service.security.permission.internal.GroupClassPermissionService;
import lombok.extern.log4j.Log4j2;
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
public class GroupClassPermissionServiceSecured extends GroupClassPermissionService {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public List<GroupClassPermission> findFor(BaseEntity entity) {
        return super.findFor(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#group, 'READ')")
    public List<GroupClassPermission> findFor(Group group) {
        return super.findFor(group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group) {
        return super.findFor(entity, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#group, 'READ')")
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, Group group) {
        return super.findFor(clazz, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#user, 'READ')")
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        return super.findFor(clazz, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group, User user) {
        return super.findFor(entity, group, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        return super.findPermissionCollectionFor(entity, group);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        return super.findPermissionCollectionFor(entity, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group, User user) {
        return super.findPermissionCollectionFor(entity, group, user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // todo: how to check permission for this?
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

    // basePermissionService methods
    // todo: add permissions for non-admins

    @Override
    @PostFilter("hasRole('ROLE_ADMIN')")
    public List<GroupClassPermission> findAll() {
        return super.findAll();
    }

    @Override
    @PostFilter("hasRole('ROLE_ADMIN')")
    public List<GroupClassPermission> findAllBy(Specification specification) {
        return super.findAllBy(specification);
    }

    @Override
    @PostAuthorize("hasRole('ROLE_ADMIN')")
    public Optional<GroupClassPermission> findOne(Long id) {
        return super.findOne(id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GroupClassPermission create(GroupClassPermission entity) {
        return super.create(entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GroupClassPermission update(Long id, GroupClassPermission entity) throws IOException {
        return super.update(id, entity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(GroupClassPermission entity) {
        super.delete(entity);
    }
}
