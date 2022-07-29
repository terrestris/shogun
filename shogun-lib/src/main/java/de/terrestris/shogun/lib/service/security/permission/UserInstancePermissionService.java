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
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserInstancePermissionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserInstancePermissionService extends BasePermissionService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Returns all {@link UserInstancePermission} for the given query arguments.
     *
     * @param user The user to find the permission for.
     * @return The permissions
     */
    public List<UserInstancePermission> findFor(User user) {

        log.trace("Getting all user instance permissions for user {}", user);

        return repository.findAllByUser(user);
    }

    /**
     * Get {@link UserInstancePermission} for SHOGun user
     *
     * @param entity The entity to find the permission for.
     * @param user The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {
        log.trace("Getting all user permissions for user with Keycloak ID {} and " +
            "entity with ID {}", user.getAuthProviderId(), entity);

        return repository.findByUserIdAndEntityId(user.getId(), entity.getId());
    }

    /**
     * Get all {@link UserInstancePermission} for the given entity.
     *
     * @param entity entity to get user permissions for
     * @return
     */
    public List<UserInstancePermission> findFor(BaseEntity entity) {
        log.trace("Getting all user permissions for entity with ID {}", entity.getId());

        return repository.findByEntityId(entity.getId());
    }

    /**
     * Returns the {@link UserInstancePermission} for the given query arguments.
     *
     * @param entity The entity to find the permission for.
     * @param permissionCollectionType The permissionCollectionType to find the permission for.
     * @return The (optional) permission.
     */
    public List<UserInstancePermission> findFor(BaseEntity entity, PermissionCollectionType permissionCollectionType) {

        log.trace("Getting all user permissions for entity with ID {} and permission " +
            "collection type {}", entity.getId(), permissionCollectionType);

        List<UserInstancePermission> result = repository
            .findByEntityAndPermissionCollectionType(entity.getId(), permissionCollectionType);

        return result;
    }

    /**
     * Returns the {@link User} that has the ADMIN permission on the given entity.
     *
     * @param entity The entity to find the owner for.
     * @return The (optional) user.
     */
    public List<User> findOwner(BaseEntity entity) {

        log.trace("Getting the owners of entity with ID {}", entity.getId());

        List<UserInstancePermission> userInstancePermission =
            this.findFor(entity, PermissionCollectionType.ADMIN);

        if (userInstancePermission.isEmpty()) {
            log.debug("No user instance permission candidate found.");

            return null;
        }

        List<User> owners = userInstancePermission.stream()
            .map(UserInstancePermission::getUser)
            .collect(Collectors.toList());

        return owners;
    }

    /**
     * Return {@link PermissionCollection} for {@link BaseEntity} and {@link User}
     * @param entity The entity to use in filter
     * @param user The user to use in filter
     * @return {@link PermissionCollection} for {@link BaseEntity} and {@link User}
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserInstancePermission> userInstancePermission = this.findFor(entity, user);

        return getPermissionCollection(userInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entity and user.
     *
     * @param persistedEntity The entity to set the permission for.
     * @param user The user to set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(BaseEntity persistedEntity, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(user, permissionCollection.get(), persistedEntity);

        UserInstancePermission userInstancePermission = new UserInstancePermission();
        userInstancePermission.setUser(user);
        userInstancePermission.setEntityId(persistedEntity.getId());
        userInstancePermission.setPermissions(permissionCollection.get());

        repository.save(userInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entities and user.
     *
     * @param persistedEntityList A collection of entities to set permission for.
     * @param user The user to set the permission for.
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE) to set.
     */
    public void setPermission(
        List<? extends BaseEntity> persistedEntityList,
        User user,
        PermissionCollectionType permissionCollectionType
    ) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        List<UserInstancePermission> userInstancePermissionsToSave = new ArrayList<>();

        persistedEntityList.forEach(e -> {
            clearExistingPermission(user, permissionCollection.get(), e);
            UserInstancePermission userInstancePermission = new UserInstancePermission();
            userInstancePermission.setUser(user);
            userInstancePermission.setEntityId(e.getId());
            userInstancePermission.setPermissions(permissionCollection.get());
            userInstancePermissionsToSave.add(userInstancePermission);
        });

        repository.saveAll(userInstancePermissionsToSave);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param user The user to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param entity The entity to clear the permission for.
     */
    private void clearExistingPermission(User user, PermissionCollection permissionCollection, BaseEntity entity) {
        Optional<UserInstancePermission> existingPermission = findFor(entity, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for entity with ID {} and user with " +
                "Keycloak ID {}: {}", entity.getId(), user.getAuthProviderId(), permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            log.debug("Removed the permission");
        }
    }

    /**
     * Deletes all {@link UserInstancePermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllFor(BaseEntity persistedEntity) {
        List<UserInstancePermission> userInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(userInstancePermissions);

        log.info("Successfully deleted all user instance permissions for entity with ID {}",
            persistedEntity.getId());
        log.trace("Deleted entity: {}", persistedEntity);
    }

    /**
     * Helper function to get the {@link PermissionCollection} from a given
     * class permission. If no collection is available, it returns an empty
     * list.
     *
     * @param classPermission The classPermission to get the permissions from.
     * @return The collection (may be empty).
     */
    private PermissionCollection getPermissionCollection(Optional<UserInstancePermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    public void deleteFor(BaseEntity persistedEntity, User user) {
        Optional<UserInstancePermission> userInstancePermission = this.findFor(persistedEntity, user);

        if (userInstancePermission.isPresent()) {
            repository.delete(userInstancePermission.get());

            log.info("Successfully deleted the user instance permission for entity with ID {} and user {}.",
                persistedEntity.getId(), user.getId());
        } else {
            log.warn("Could not delete the user instance permission. The requested permission does not exist.");
        }
    }

}
