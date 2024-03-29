/*
 * SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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
import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserClassPermissionRepository;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserClassPermissionService extends BasePermissionService<UserClassPermissionRepository, UserClassPermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    private UserProviderService userProviderService;

    /**
     * Returns all {@link UserClassPermission} for the given query arguments.
     *
     * @param user The user to find the permissions for.
     * @return The permissions.
     */
    public List<UserClassPermission> findFor(User user) {
        log.trace("Getting all user class permissions for user with Keycloak ID {}",
            user.getAuthProviderId());

        List<UserClassPermission> permissions = repository.findAllByUser(user);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Get all {@link UserClassPermission} for the given entity.
     *
     * @param entity entity to get user permissions for
     * @return
     */
    public List<UserClassPermission> findFor(BaseEntity entity) {
        String className = entity.getClass().getCanonicalName();

        log.trace("Getting all user class permissions for entity class {}", className);

        List<UserClassPermission> permissions = repository.findByClassName(className);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Return {@link Optional} containing {@link UserClassPermission}
     *
     * @param clazz The class that should be checked
     * @param user  The user to check for
     * @return {@link Optional} containing {@link UserClassPermission}
     */
    public Optional<UserClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        String className = clazz.getCanonicalName();

        log.trace("Getting all user class permissions for user with Keycloak ID {} and " +
            "entity class {}", user.getAuthProviderId(), className);

        Optional<UserClassPermission> permission = repository.findByUserIdAndClassName(user.getId(), className);

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link UserClassPermission} for the given query arguments. Hereby
     * the class of the given entity will be considered.
     *
     * @param entity The entity to find the permission for.
     * @param user   The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserClassPermission> findFor(BaseEntity entity, User user) {
        log.trace("Getting all user class permissions for user with Keycloak ID {} and " +
            "entity class {}", user.getAuthProviderId(), entity.getClass().getCanonicalName());

        Optional<UserClassPermission> permission = repository.findByUserIdAndClassName(user.getId(), entity.getClass().getCanonicalName());

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments. Hereby
     * the class of the given entity and and all groups of the given user will be considered.
     *
     * @param entity The entity to find the collection for.
     * @param user   The user to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Class<? extends BaseEntity> clazz = entity.getClass();
        Optional<UserClassPermission> userClassPermission = this.findFor(clazz, user);

        return getPermissionCollection(userClassPermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given class and the currently
     * logged in user.
     *
     * @param clazz                    The class to set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = userProviderService.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(clazz, activeUser.get(), permissionCollectionType);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given class and user.
     *
     * @param clazz                    The class to find set the permission for.
     * @param user                     The user to find set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(user, permissionCollection.get(), clazz);

        UserClassPermission userClassPermission = new UserClassPermission();
        userClassPermission.setUser(user);
        userClassPermission.setClassName(clazz.getCanonicalName());
        userClassPermission.setPermission(permissionCollection.get());

        repository.save(userClassPermission);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param user                 The user to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param clazz                The class to clear the permission for.
     */
    private void clearExistingPermission(User user, PermissionCollection permissionCollection, Class<? extends BaseEntity> clazz) {
        Optional<UserClassPermission> existingPermission = findFor(clazz, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for clazz {} and user with " +
                "Keycloak ID {}: {}", clazz.getCanonicalName(), user, permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            log.debug("Removed the permission");
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
    private PermissionCollection getPermissionCollection(Optional<UserClassPermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermission();
        }

        return new PermissionCollection();
    }

    /**
     * Deletes all {@link UserClassPermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllFor(BaseEntity persistedEntity) {
        List<UserClassPermission> userClassPermissions = this.findFor(persistedEntity);

        repository.deleteAll(userClassPermissions);

        log.info("Successfully deleted all user class permissions for entity with ID {}",
            persistedEntity.getId());
    }

    /**
     * Deletes all {@link UserClassPermission} for the given user.
     *
     * @param user The entity to clear the permissions for.
     */
    public void deleteAllFor(User user) {
        List<UserClassPermission> userClassPermissions = this.findFor(user);

        repository.deleteAll(userClassPermissions);

        log.info("Successfully deleted all user class permissions for user with ID {}",
            user.getId());
    }

    public void deleteFor(BaseEntity persistedEntity, User user) {
        Optional<UserClassPermission> userClassPermission = this.findFor(persistedEntity.getClass(), user);

        if (userClassPermission.isPresent()) {
            repository.delete(userClassPermission.get());

            log.info("Successfully deleted the user class permission for entity with ID {} and user {}.",
                persistedEntity.getId(), user.getId());
        } else {
            log.warn("Could not delete the user class permission. The requested permission does not exist.");
        }
    }

    private void setAuthProviderRepresentation(UserClassPermission permission) {
        userProviderService.setTransientRepresentations(permission.getUser());
    }

    private void setAuthProviderRepresentation(List<UserClassPermission> permissions) {
        permissions.forEach((userClassPermission -> setAuthProviderRepresentation(userClassPermission)));
    }
}
