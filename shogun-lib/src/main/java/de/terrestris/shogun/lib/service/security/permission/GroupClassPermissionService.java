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
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.GroupClassPermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class GroupClassPermissionService extends BasePermissionService<GroupClassPermissionRepository, GroupClassPermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    private GroupProviderService groupProviderService;

    /**
     * Returns all {@link GroupClassPermission} for the given query arguments.
     *
     * @param entity The base entity to find the permissions for.
     * @return The permissions.
     */
    public List<GroupClassPermission> findFor(BaseEntity entity) {
        log.trace("Getting all group class permissions for entity with ID {}", entity.getId());

        List<GroupClassPermission> permissions = repository.findByClassName(entity.getClass().getCanonicalName());

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Returns all {@link GroupClassPermission} for the given query arguments.
     *
     * @param group The group to find the permissions for.
     * @return The permissions.
     */
    public List<GroupClassPermission> findFor(Group group) {
        log.trace("Getting all group class permissions for group with Keycloak ID {}",
            group.getAuthProviderId());

        List<GroupClassPermission> permissions = repository.findAllByGroup(group);

        setAuthProviderRepresentation(permissions);

        return permissions;
    }

    /**
     * Find group class permission for class of entity and given group
     *
     * @param entity The entity to find the permission for.
     * @param group  The group to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group) {
        String className = entity.getClass().getCanonicalName();

        log.trace("Getting all group class permissions for group with Keycloak ID {} and " +
            "entity class {}", group.getAuthProviderId(), className);

        Optional<GroupClassPermission> permission = repository.findByGroupIdAndClassName(group.getId(), className);

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link GroupClassPermission} for the given query arguments.
     *
     * @param clazz The class to find the permission for.
     * @param group The group to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, Group group) {
        String className = clazz.getCanonicalName();

        log.trace("Getting all group class permissions for group with Keycloak ID {} and " +
            "entity class {}", group.getAuthProviderId(), className);

        Optional<GroupClassPermission> permission = repository.findByGroupIdAndClassName(group.getId(), className);

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link GroupClassPermission} for the given query arguments. Hereby
     * all groups of the given user will be considered.
     *
     * @param clazz The class to find the permission for.
     * @param user  The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        String className = clazz.getCanonicalName();

        log.trace("Getting all group class permissions for user with Keycloak ID {} and " +
            "entity class {}", user.getAuthProviderId(), className);

        // Get all groups of the user from Keycloak
        List<Group> groups = groupProviderService.findByUser(user);
        Optional<GroupClassPermission> gcp = Optional.empty();
        if (groups == null) {
            return gcp;
        }

        for (Group g : groups) {
            Optional<GroupClassPermission> permissionsForGroup = repository
                .findByGroupIdAndClassName(g.getId(), className);

            if (permissionsForGroup.isPresent()) {
                gcp = permissionsForGroup;
                setAuthProviderRepresentation(gcp.get());
                break;
            }
        }

        return gcp;
    }

    /**
     * Returns the {@link GroupClassPermission} for the given query arguments. Hereby
     * it will be considered if the user is currently a member of the given group.
     *
     * @param entity The entity to find the permission for.
     * @param group  The group to find the permission for.
     * @param user   The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group, User user) {
        log.trace("Getting all group class permissions for user with Keycloak ID {} and " +
                "entity with ID {} in the context of group with Keycloak ID {}", user.getAuthProviderId(),
            entity.getId(), group.getAuthProviderId());

        List<Group> userGroups = groupProviderService.findByUser(user);
        boolean isUserMemberInGroup = userGroups.contains(group);

        if (!isUserMemberInGroup) {
            log.trace("The user is not a member of the given group, no permissions available.");

            return Optional.empty();
        }

        Optional<GroupClassPermission> permission = repository.findByGroupIdAndClassName(group.getId(), entity.getClass().getCanonicalName());

        if (permission.isPresent()) {
            setAuthProviderRepresentation(permission.get());
        }

        return permission;
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments.
     *
     * @param entity The entity to find the collection for.
     * @param group  The group to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(entity, group);

        return getPermissionCollection(groupClassPermission);
    }

    /**
     * Returns the {@link GroupClassPermission} for the given query arguments. Hereby
     * the class of the given entity and all groups of the given user will be considered.
     *
     * @param entity The entity to find the permission for.
     * @param user   The user to find the permission for.
     * @return The (optional) permission.
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Class<? extends BaseEntity> clazz = entity.getClass();
        Optional<GroupClassPermission> groupClassPermission = this.findFor(clazz, user);

        return getPermissionCollection(groupClassPermission);
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments. Hereby
     * it will be considered if the user is currently a member of the given group.
     *
     * @param entity The entity to find the collection for.
     * @param group  The group to find the collection for.
     * @param user   The user to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group, User user) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(entity, group, user);

        return getPermissionCollection(groupClassPermission);
    }

    /**
     * Sets the {@link PermissionCollection} for the given target combination.
     *
     * @param clazz                    The class to set the permission for.
     * @param group                    The group to set the permission for.
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE) to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, Group group, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository
            .findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(group, permissionCollection.get(), clazz);

        GroupClassPermission groupClassPermission = new GroupClassPermission();
        groupClassPermission.setGroup(group);
        groupClassPermission.setClassName(clazz.getCanonicalName());
        groupClassPermission.setPermission(permissionCollection.get());

        repository.save(groupClassPermission);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param group                The group to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param clazz                The clazz to clear the permission for.
     */
    private void clearExistingPermission(Group group, PermissionCollection permissionCollection, Class<? extends BaseEntity> clazz) {
        Optional<GroupClassPermission> existingPermission = findFor(clazz, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for class {} and group with " +
                "Keycloak ID {}: {}", clazz, group.getAuthProviderId(), permissionCollection);

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
    private PermissionCollection getPermissionCollection(Optional<GroupClassPermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermission();
        }

        return new PermissionCollection();
    }

    public void deleteAllFor(BaseEntity persistedEntity) {
        List<GroupClassPermission> groupClassPermissions = this.findFor(persistedEntity);

        repository.deleteAll(groupClassPermissions);

        log.info("Successfully deleted all group class permissions for entity with ID {}",
            persistedEntity.getId());
    }

    public void deleteFor(BaseEntity persistedEntity, Group group) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(persistedEntity, group);

        if (groupClassPermission.isPresent()) {
            repository.delete(groupClassPermission.get());

            log.info("Successfully deleted the group class permission for entity with ID {} and group {}.",
                persistedEntity.getId(), group.getId());
        } else {
            log.warn("Could not delete the group class permission. The requested permission does not exist.");
        }
    }

    private void setAuthProviderRepresentation(GroupClassPermission permission) {
        groupProviderService.setTransientRepresentations(permission.getGroup());
    }

    private void setAuthProviderRepresentation(List<GroupClassPermission> permissions) {
        permissions.forEach((groupClassPermission -> setAuthProviderRepresentation(groupClassPermission)));
    }
}
