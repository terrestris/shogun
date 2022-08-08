/*
 * SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.GroupInstancePermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class GroupInstancePermissionService extends BasePermissionService<GroupInstancePermissionRepository, GroupInstancePermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    private GroupProviderService groupProviderService;

    /**
     * Returns all {@link GroupInstancePermission} for the given query arguments.
     *
     * @param group The group to find the permissions for.
     * @return The permissions.
     */
    public List<GroupInstancePermission> findFor(Group group) {

        log.trace("Getting all group instance permissions for group with Keycloak ID {}",
            group.getAuthProviderId());

        return repository.findAllByGroup(group);
    }

    /**
     * Get permission for SHOGun group
     *
     * @param entity entity to get group permissions for
     * @param group  The SHOGun group
     * @return
     */
    public Optional<GroupInstancePermission> findFor(BaseEntity entity, Group group) {
        if (entity == null || group == null) {
            log.trace("Either entity or group is null");
            return Optional.empty();
        }

        if (entity.getId() == null || group.getId() == null) {
            log.trace("Either entity or group is not persisted yet.");
            return Optional.empty();
        }

        log.trace("Getting all group permissions for group with Keycloak ID {} and " +
            "entity with ID {}", group.getAuthProviderId(), entity.getId());

        return repository.findByGroupIdAndEntityId(group.getId(), entity.getId());
    }

    /**
     * Returns the {@link GroupInstancePermission} for the given query arguments.
     *
     * @param entity The entity to find the permission for.
     * @return The (optional) permission.
     */
    public List<GroupInstancePermission> findFor(BaseEntity entity) {
        log.trace("Getting all group permissions for entity with ID {}", entity.getId());

        return repository.findByEntityId(entity.getId());
    }

    /**
     * Returns the {@link GroupInstancePermission} for the given query arguments. Hereby
     * all groups of the given user will be considered.
     *
     * @param entity The entity to find the permission for.
     * @param user   The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupInstancePermission> findFor(BaseEntity entity, User user) {
        log.trace("Getting all group permissions for user with Keycloak ID {} and " +
            "entity with ID {}", user.getAuthProviderId(), entity.getId());

        // Get all groups of the user from Keycloak
        List<Group> groups = groupProviderService.findByUser(user);
        Optional<GroupInstancePermission> gip = Optional.empty();
        if (groups == null) {
            return gip;
        }
        for (Group g : groups) {
            Optional<GroupInstancePermission> permissionsForGroup = repository
                .findByGroupIdAndEntityId(g.getId(), entity.getId());
            if (permissionsForGroup.isPresent()) {
                gip = permissionsForGroup;
                break;
            }
        }
        return gip;
    }

    /**
     * Returns the {@link GroupInstancePermission} for the given query arguments. Hereby
     * it will be considered if the user is currently a member of the given group.
     *
     * @param entity The entity to find the permission for.
     * @param group  The group to find the permission for.
     * @param user   The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<GroupInstancePermission> findFor(BaseEntity entity, Group group, User user) {

        log.trace("Getting all group instance permissions for user with Keycloak ID {} " +
                "and entity with ID {} in the context of group with Keycloak ID {}",
            user.getAuthProviderId(), entity.getId(), group.getAuthProviderId());

        List<Group> userGroups = groupProviderService.findByUser(user);
        boolean isUserMemberInGroup = userGroups.contains(group);

        if (!isUserMemberInGroup) {
            log.trace("The user is not a member of the given group, no permissions available.");

            return Optional.empty();
        }

        return repository.findByGroupIdAndEntityId(group.getId(), entity.getId());
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments.
     *
     * @param entity The entity to find the collection for.
     * @param group  The group to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, group);

        return getPermissionCollection(groupInstancePermission);
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments. Hereby
     * all groups of the given user will be considered.
     *
     * @param entity The entity to find the collection for.
     * @param user   The user to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, user);

        return getPermissionCollection(groupInstancePermission);
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
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, group, user);

        return getPermissionCollection(groupInstancePermission);
    }

    /**
     * Sets the {@link PermissionCollection} for the given target combination.
     *
     * @param persistedEntity          The entity to set the permission for.
     * @param group                    The group to set the permission for.
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE) to set.
     */
    public void setPermission(BaseEntity persistedEntity, Group group, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(group, permissionCollection.get(), persistedEntity);

        GroupInstancePermission groupInstancePermission = new GroupInstancePermission();
        groupInstancePermission.setGroup(group);
        groupInstancePermission.setEntityId(persistedEntity.getId());
        groupInstancePermission.setPermission(permissionCollection.get());

        repository.save(groupInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entities and user.
     *
     * @param persistedEntityList      A collection of entities to set permission for.
     * @param group                    The group to set the permission for.
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE) to set.
     */
    public void setPermission(
        List<? extends BaseEntity> persistedEntityList,
        Group group,
        PermissionCollectionType permissionCollectionType
    ) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        List<GroupInstancePermission> groupInstancePermissionsToSave = new ArrayList<>();

        persistedEntityList.forEach(e -> {
            clearExistingPermission(group, permissionCollection.get(), e);
            GroupInstancePermission groupInstancePermission = new GroupInstancePermission();
            groupInstancePermission.setGroup(group);
            groupInstancePermission.setEntityId(e.getId());
            groupInstancePermission.setPermission(permissionCollection.get());
            groupInstancePermissionsToSave.add(groupInstancePermission);
        });

        repository.saveAll(groupInstancePermissionsToSave);
    }

    /**
     * Clears the given {@link PermissionCollection} for the given target combination.
     *
     * @param group                The group to clear the permission for.
     * @param permissionCollection The permission collection to clear.
     * @param entity               The entity to clear the permission for.
     */
    private void clearExistingPermission(Group group, PermissionCollection permissionCollection, BaseEntity entity) {
        Optional<GroupInstancePermission> existingPermission = findFor(entity, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            log.debug("Permission is already set for entity with ID {} and group with " +
                "Keycloak ID {}: {}", entity.getId(), group.getAuthProviderId(), permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            log.debug("Removed the permission");
        }
    }

    /**
     * Deletes all {@link GroupInstancePermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllFor(BaseEntity persistedEntity) {
        List<GroupInstancePermission> groupInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(groupInstancePermissions);

        log.info("Successfully deleted all group instance permissions for entity with ID {}",
            persistedEntity.getId());
        log.trace("Deleted entity: {}", persistedEntity);
    }

    public void deleteFor(BaseEntity persistedEntity, Group group) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(persistedEntity, group);

        if (groupInstancePermission.isPresent()) {
            repository.delete(groupInstancePermission.get());

            log.info("Successfully deleted the group instance permission for entity with ID {} and group {}.",
                persistedEntity.getId(), group.getId());
        } else {
            log.warn("Could not delete the group instance permission. The requested permission does not exist.");
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
    private PermissionCollection getPermissionCollection(Optional<GroupInstancePermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermission();
        }

        return new PermissionCollection();
    }
}
