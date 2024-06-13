/*
 * SHOGun, https://terrestris.github.io/shogun/
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

package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.model.security.permission.RoleClassPermission;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.RoleClassPermissionRepository;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class RoleClassPermissionService extends BasePermissionService<RoleClassPermissionRepository, RoleClassPermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    private RoleProviderService roleProviderService;

    /**
     * Returns all {@link RoleClassPermission} for the given query arguments.
     *
     * @param role The role to find the permissions for.
     * @return The permissions.
     */
    public List<RoleClassPermission> findFor(Role role) {
        log.trace("Getting all role class permissions for role with Keycloak ID {}",
            role.getAuthProviderId());

        List<RoleClassPermission> permissions = repository.findAllByRole(role);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Get all {@link RoleClassPermission} for the given entity.
     *
     * @param entity entity to get role permissions for
     * @return
     */
    public List<RoleClassPermission> findFor(BaseEntity entity) {
        String className = entity.getClass().getCanonicalName();

        log.trace("Getting all role class permissions for entity class {}", className);

        List<RoleClassPermission> permissions = repository.findByClassName(className);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Return {@link Optional} containing {@link RoleClassPermission}
     *
     * @param clazz The class that should be checked
     * @param role  The role to check for
     * @return {@link Optional} containing {@link RoleClassPermission}
     */
    public Optional<RoleClassPermission> findFor(Class<? extends BaseEntity> clazz, Role role) {
        String className = clazz.getCanonicalName();

        log.trace("Getting all role class permissions for role with Keycloak ID {} and " +
            "entity class {}", role.getAuthProviderId(), className);

        Optional<RoleClassPermission> permission = repository.findByRoleIdAndClassName(role.getId(), className);

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link RoleClassPermission} for the given query arguments. Hereby
     * the class of the given entity will be considered.
     *
     * @param entity The entity to find the permission for.
     * @param role   The role to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<RoleClassPermission> findFor(BaseEntity entity, Role role) {
        log.trace("Getting all role class permissions for role with Keycloak ID {} and " +
            "entity class {}", role.getAuthProviderId(), entity.getClass().getCanonicalName());

        Optional<RoleClassPermission> permission = repository.findByRoleIdAndClassName(role.getId(), entity.getClass().getCanonicalName());

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments.
     *
     * @param entity The entity to find the collection for.
     * @param role   The role to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Role role) {
        Class<? extends BaseEntity> clazz = entity.getClass();
        Optional<RoleClassPermission> roleClassPermission = this.findFor(clazz, role);

        return getPermissionCollection(roleClassPermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given class and role.
     *
     * @param clazz                    The class to find set the permission for.
     * @param role                     The role to find set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, Role role, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(role, permissionCollection.get(), clazz);

        RoleClassPermission roleClassPermission = new RoleClassPermission();
        roleClassPermission.setRole(role);
        roleClassPermission.setClassName(clazz.getCanonicalName());
        roleClassPermission.setPermission(permissionCollection.get());

        repository.save(roleClassPermission);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param role                 The role to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param clazz                The class to clear the permission for.
     */
    private void clearExistingPermission(Role role, PermissionCollection permissionCollection, Class<? extends BaseEntity> clazz) {
        Optional<RoleClassPermission> existingPermission = findFor(clazz, role);

        // Check if there is already an existing permission set on the entity.
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for clazz {} and role with " +
                "Keycloak ID {}: {}", clazz.getCanonicalName(), role.getAuthProviderId(), permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            log.debug("Removed the permission");
        }
    }

    /**
     * Deletes all {@link RoleClassPermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllFor(BaseEntity persistedEntity) {
        List<RoleClassPermission> roleClassPermissions = this.findFor(persistedEntity);

        repository.deleteAll(roleClassPermissions);

        log.info("Successfully deleted all role class permissions for entity with ID {}",
            persistedEntity.getId());
    }

    /**
     * Deletes all {@link RoleClassPermission} for the given role.
     *
     * @param role The role to clear the permissions for.
     */
    public void deleteAllFor(Role role) {
        List<RoleClassPermission> roleClassPermissions = this.findFor(role);

        repository.deleteAll(roleClassPermissions);

        log.info("Successfully deleted all role class permissions for role with ID {}",
            role.getId());
    }

    /**
     * Deletes the {@link RoleClassPermission} for the given entity and role.
     *
     * @param persistedEntity The entity to clear the permissions for.
     * @param role            The role to clear the permission for.
     */
    public void deleteFor(BaseEntity persistedEntity, Role role) {
        Optional<RoleClassPermission> roleClassPermission = this.findFor(persistedEntity.getClass(), role);

        if (roleClassPermission.isPresent()) {
            repository.delete(roleClassPermission.get());

            log.info("Successfully deleted the role class permission for entity with ID {} and role {}.",
                persistedEntity.getId(), role.getId());
        } else {
            log.warn("Could not delete the role class permission. The requested permission does not exist.");
        }
    }

    /**
     * Helper function to get the {@link PermissionCollection} from a given
     * class permission. If no collection is available, it returns an empty
     * list.
     *
     * @param classPermission The classPermission to get the permissions from.
     * @return The collection (may be empty).
     */
    private PermissionCollection getPermissionCollection(Optional<RoleClassPermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermission();
        }

        return new PermissionCollection();
    }

    private void setAuthProviderRepresentation(RoleClassPermission permission) {
        roleProviderService.setTransientRepresentations(permission.getRole());
    }

    private void setAuthProviderRepresentation(List<RoleClassPermission> permissions) {
        permissions.forEach((roleClassPermission -> setAuthProviderRepresentation(roleClassPermission)));
    }
}
