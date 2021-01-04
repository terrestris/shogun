package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.GroupClassPermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupClassPermissionService extends BaseService<GroupClassPermissionRepository, GroupClassPermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Find group class permission for class of entity and given group
     *
     * @param entity The entity whose class should be checked
     * @param group The group
     * @return An Optional containing the GroupClassPermission
     */
    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group) {
        String className = entity.getClass().getCanonicalName();
        LOG.trace("Getting all group class permissions for group {} and entity class {}",
            group.getKeycloakId(), className);
        return repository.findByGroupIdAndClassName(group.getId(), className);
    }

    /**
     * Find group class permission for class and given group
     *
     * @param clazz The class that should be checked
     * @param group The group
     * @return An Optional containing the GroupClassPermission
     */
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, Group group) {
        String className = clazz.getCanonicalName();
        LOG.trace("Getting all group class permissions for group {} and entity class {}",
            group.getKeycloakId(), className);
        return repository.findByGroupIdAndClassName(group.getId(), className);
    }

    /**
     * Find first group class permission for class and given user.
     * Iterates over all group the user is member of
     *
     * @param clazz The class that should be checked
     * @param user The user to check for
     * @return An Optional containing the GroupClassPermission
     */
    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        String className = clazz.getCanonicalName();
        LOG.trace("Getting all group class permissions for user {} and entity class {}",
            user.getKeycloakId(), className);

        // Get all groups of the user from Keycloak
        List<Group> groups = securityContextUtil.getGroupsForUser(user);
        Optional<GroupClassPermission> gcp = Optional.empty();
        for (Group g : groups) {
            Optional<GroupClassPermission> permissionsForGroup = repository.findByGroupIdAndClassName(g.getId(), className);
            if (permissionsForGroup.isPresent()) {
                gcp = permissionsForGroup;
                break;
            }
        }

        return gcp;
    }

    /**
     * Return permission collection for base entity and user based on class type and user groups
     *
     * @param entity The entity whose class should be checked
     * @param user The user to get groups from
     * @return A permission collection
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Class<? extends BaseEntity> clazz = entity.getClass();
        Optional<GroupClassPermission> groupClassPermission = this.findFor(clazz, user);

        if (groupClassPermission.isPresent()) {
            return groupClassPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    /**
     * Set Permission for SHOGun group
     * @param clazz The class to set permission for
     * @param group The SHOGun group
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE)
     */
    public void setPermission(Class<? extends BaseEntity> clazz, Group group, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findByName(permissionCollectionType);

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        clearExistingPermission(group, permissionCollection.get(), clazz);

        GroupClassPermission groupClassPermission = new GroupClassPermission();
        groupClassPermission.setGroup(group);
        groupClassPermission.setClassName(clazz.getCanonicalName());
        groupClassPermission.setPermissions(permissionCollection.get());

        repository.save(groupClassPermission);
    }

    private void clearExistingPermission(Group group, PermissionCollection permissionCollection, Class<? extends BaseEntity> clazz) {
        Optional<GroupClassPermission> existingPermission = findFor(clazz, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            LOG.debug("Permission is already set for class {} and group {}: {}", clazz,
                group, permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            LOG.debug("Removed the permission");
        }
    }
}
