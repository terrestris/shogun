package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.GroupInstancePermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupInstancePermissionService extends BaseService<GroupInstancePermissionRepository, GroupInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Get permission for SHOGun group
     * @param entity entity to get group permissions for
     * @param group The SHOGun group
     * @return
     */
    public Optional<GroupInstancePermission> findFor(BaseEntity entity, Group group) {
        if (entity == null || group == null) {
            LOG.trace("Either entity or group is null");
            return Optional.empty();
        }

        if (entity.getId() == null || group.getId() == null) {
            LOG.trace("Either entity or group is not persisted yet.");
            return Optional.empty();
        }

        LOG.trace("Getting all group permissions for group {} and entity {}", group.getKeycloakId(), entity);

        return repository.findByGroupIdAndEntityId(group.getId(), entity.getId()); // TODO: !
    }


    /**
     * Get all {@link GroupInstancePermission} for the given entity.
     *
     * @param entity entity to get group permissions for
     * @return
     */
    public List<GroupInstancePermission> findFor(BaseEntity entity) {
        LOG.trace("Getting all group permissions for entity {}", entity);

        return repository.findByEntityId(entity.getId());
    }

    /**
     * Get groups of user from Keycloak and return resulting permissions
     * @param entity entity to get group permissions for
     * @param user The SHOGun user
     * @return
     */
    public Optional<GroupInstancePermission> findFor(BaseEntity entity, User user) {
        LOG.trace("Getting all group permissions for user {} and entity {}", user.getKeycloakId(), entity);
        // Get all groups of the user from Keycloak
        List<Group> groups = securityContextUtil.getGroupsForUser(user);
        Optional<GroupInstancePermission> gip = Optional.empty();
        for (Group g : groups) {
            Optional<GroupInstancePermission> permissionsForGroup = repository.findByGroupIdAndEntityId(g.getId(), entity.getId());
            if (permissionsForGroup.isPresent()) {
                gip = permissionsForGroup;
                break;
            }
        }

        return gip;
    }

    /**
     * Return permission collection for base entity and group
     * @param entity The entity to fetch permission collection for
     * @param user The user
     * @return The first permission collection of the user that can be found for this entity
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, user);

        if (groupInstancePermission.isPresent()) {
            return groupInstancePermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    /**
     * Set Permission for SHOGun group
     * @param persistedEntity The entity to set permission for
     * @param group The SHOGun group
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE)
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
        groupInstancePermission.setPermissions(permissionCollection.get());

        repository.save(groupInstancePermission);
    }

    /**
     * Set Permission for SHOGun group for multiple entities at once
     * @param persistedEntityList A collection of entities to set permission for
     * @param group The SHOGun group
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE)
     */
    public void setPermissionBulk(
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
            groupInstancePermission.setPermissions(permissionCollection.get());
            groupInstancePermissionsToSave.add(groupInstancePermission);
        });

        repository.saveAll(groupInstancePermissionsToSave);
    }

    private void clearExistingPermission(Group group, PermissionCollection permissionCollection, BaseEntity entity) {
        Optional<GroupInstancePermission> existingPermission = findFor(entity, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            LOG.debug("Permission is already set for entity {} and group {}: {}", entity,
                group, permissionCollection);

            // Remove the existing one
            // TODO: deletion really needed ???
            repository.delete(existingPermission.get());

            LOG.debug("Removed the permission");
        }
    }

    public void deleteAllForEntity(BaseEntity persistedEntity) {
        List<GroupInstancePermission> groupInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(groupInstancePermissions);

        LOG.info("Successfully deleted all group instance permissions for entity with id {}", persistedEntity.getId());
        LOG.trace("Deleted entity: {}", persistedEntity);
    }
}
