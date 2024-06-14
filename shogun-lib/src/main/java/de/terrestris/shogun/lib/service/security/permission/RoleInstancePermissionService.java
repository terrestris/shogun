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
import de.terrestris.shogun.lib.model.security.permission.RoleInstancePermission;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.RoleInstancePermissionRepository;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RoleInstancePermissionService extends BasePermissionService<RoleInstancePermissionRepository, RoleInstancePermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    private RoleProviderService roleProviderService;

    /**
     * Returns all {@link RoleInstancePermission} for the given query arguments.
     *
     * @param role The role to find the permission for.
     * @return The permissions
     */
    public List<RoleInstancePermission> findFor(Role role) {
        log.trace("Getting all role instance permissions for role {}", role);

        List<RoleInstancePermission> permissions = repository.findAllByRole(role);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Get {@link RoleInstancePermission} for SHOGun role.
     *
     * @param entity The entity to find the permission for.
     * @param role   The role to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<RoleInstancePermission> findFor(BaseEntity entity, Role role) {
        log.trace("Getting all role permissions for role with Keycloak ID {} and " +
            "entity with ID {}", role.getAuthProviderId(), entity);

        Optional<RoleInstancePermission> permission = repository.findByRoleIdAndEntityId(
            role.getId(), entity.getId());

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Get all {@link RoleInstancePermission} for the given entity.
     *
     * @param entity entity to get role permissions for
     * @return
     */
    public List<RoleInstancePermission> findFor(BaseEntity entity) {
        log.trace("Getting all role permissions for entity with ID {}", entity.getId());

        List<RoleInstancePermission> permissions = repository.findByEntityId(entity.getId());

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Returns the {@link RoleInstancePermission} for the given query arguments.
     *
     * @param entity                   The entity to find the permission for.
     * @param permissionCollectionType The permissionCollectionType to find the permission for.
     * @return The (optional) permission.
     */
    public List<RoleInstancePermission> findFor(BaseEntity entity, PermissionCollectionType permissionCollectionType) {

        log.trace("Getting all role permissions for entity with ID {} and permission " +
            "collection type {}", entity.getId(), permissionCollectionType);

        List<RoleInstancePermission> permissions = repository
            .findByEntityAndPermissionCollectionType(entity.getId(), permissionCollectionType);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Returns the {@link Role} that has the ADMIN permission on the given entity.
     *
     * @param entity The entity to find the owner role for.
     * @return The (optional) role.
     */
    public List<Role> findOwner(BaseEntity entity) {

        log.trace("Getting the owner roles of entity with ID {}", entity.getId());

        List<RoleInstancePermission> roleInstancePermission =
            this.findFor(entity, PermissionCollectionType.ADMIN);

        if (roleInstancePermission.isEmpty()) {
            log.debug("No role instance permission candidate found.");

            return null;
        }

        List<Role> owners = roleInstancePermission.stream()
            .map(RoleInstancePermission::getRole)
            .collect(Collectors.toList());

        return owners;
    }

    /**
     * Return {@link PermissionCollection} for {@link BaseEntity} and {@link Role}
     *
     * @param entity The entity to use in filter
     * @param role   The role to use in filter
     * @return {@link PermissionCollection} for {@link BaseEntity} and {@link Role}
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Role role) {
        Optional<RoleInstancePermission> roleInstancePermission = this.findFor(entity, role);

        return getPermissionCollection(roleInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entity and role.
     *
     * @param persistedEntity          The entity to set the permission for.
     * @param role                     The role to set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(BaseEntity persistedEntity, Role role, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(role, permissionCollection.get(), persistedEntity);

        RoleInstancePermission roleInstancePermission = new RoleInstancePermission();
        roleInstancePermission.setRole(role);
        roleInstancePermission.setEntityId(persistedEntity.getId());
        roleInstancePermission.setPermission(permissionCollection.get());

        repository.save(roleInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entities and role.
     *
     * @param persistedEntityList      A collection of entities to set permission for.
     * @param role                     The role to set the permission for.
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE) to set.
     */
    public void setPermission(
        List<? extends BaseEntity> persistedEntityList,
        Role role,
        PermissionCollectionType permissionCollectionType
    ) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        List<RoleInstancePermission> roleInstancePermissionsToSave = new ArrayList<>();

        persistedEntityList.forEach(e -> {
            clearExistingPermission(role, permissionCollection.get(), e);
            RoleInstancePermission roleInstancePermission = new RoleInstancePermission();
            roleInstancePermission.setRole(role);
            roleInstancePermission.setEntityId(e.getId());
            roleInstancePermission.setPermission(permissionCollection.get());
            roleInstancePermissionsToSave.add(roleInstancePermission);
        });

        repository.saveAll(roleInstancePermissionsToSave);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param role                 The role to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param entity               The entity to clear the permission for.
     */
    private void clearExistingPermission(Role role, PermissionCollection permissionCollection, BaseEntity entity) {
        Optional<RoleInstancePermission> existingPermission = findFor(entity, role);

        // Check if there is already an existing permission set on the entity.
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for entity with ID {} and role with " +
                "Keycloak ID {}: {}", entity.getId(), role.getAuthProviderId(), permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            log.debug("Removed the permission");
        }
    }

    /**
     * Deletes all {@link RoleInstancePermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllFor(BaseEntity persistedEntity) {
        List<RoleInstancePermission> roleInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(roleInstancePermissions);

        log.info("Successfully deleted all role instance permissions for entity with ID {}",
            persistedEntity.getId());
        log.trace("Deleted entity: {}", persistedEntity);
    }

    /**
     * Deletes all {@link RoleInstancePermission} for the given role.
     *
     * @param role The role to clear the permissions for.
     */
    public void deleteAllFor(Role role) {
        List<RoleInstancePermission> roleInstancePermissions = this.findFor(role);

        repository.deleteAll(roleInstancePermissions);

        log.info("Successfully deleted all role instance permissions for role with ID {}",
            role.getId());
    }

    /**
     * Deletes the {@link RoleInstancePermission} for the given entity and role.
     *
     * @param persistedEntity The entity to clear the permissions for.
     * @param role            The role to clear the permission for.
     */
    public void deleteFor(BaseEntity persistedEntity, Role role) {
        Optional<RoleInstancePermission> roleInstancePermission = this.findFor(persistedEntity, role);

        if (roleInstancePermission.isPresent()) {
            repository.delete(roleInstancePermission.get());

            log.info("Successfully deleted the role instance permission for entity with ID {} and role {}.",
                persistedEntity.getId(), role.getId());
        } else {
            log.warn("Could not delete the role instance permission. The requested permission does not exist.");
        }
    }

    /**
     * Helper function to get the {@link PermissionCollection} from a given
     * class permission. If no collection is available, it returns an empty
     * list.
     *
     * @param rolePermission The rolePermission to get the permissions from.
     * @return The collection (may be empty).
     */
    private PermissionCollection getPermissionCollection(Optional<RoleInstancePermission> rolePermission) {
        if (rolePermission.isPresent()) {
            return rolePermission.get().getPermission();
        }

        return new PermissionCollection();
    }

    private void setAuthProviderRepresentation(RoleInstancePermission permission) {
        roleProviderService.setTransientRepresentations(permission.getRole());
    }

    private void setAuthProviderRepresentation(List<RoleInstancePermission> permissions) {
        permissions.forEach((roleInstancePermission -> setAuthProviderRepresentation(roleInstancePermission)));
    }
}
