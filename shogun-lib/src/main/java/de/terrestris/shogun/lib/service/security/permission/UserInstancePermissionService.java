package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserInstancePermissionRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserInstancePermissionService extends BaseService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Get {@link UserInstancePermission} for SHOGun user
     *
     * @param entity entity to get group permissions for
     * @param user The SHOGun user
     * @return
     */
    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {
        LOG.trace("Getting all user permissions for user {} and entity {}", user.getKeycloakId(), entity);
        return repository.findByUserIdAndEntityId(user.getId(), entity.getId());
    }

    /**
     * Get all {@link UserInstancePermission} for the given entity.
     *
     * @param entity entity to get user permissions for
     * @return
     */
    public List<UserInstancePermission> findFor(BaseEntity entity) {
        LOG.trace("Getting all user permissions for entity {}", entity);

        return repository.findByEntityId(entity.getId());
    }

    /**
     * Return {@link PermissionCollection} for {@link BaseEntity} and {@link User}
     * @param entity The entity to use in filter
     * @param user The user to use in filter
     * @return {@link PermissionCollection} for {@link BaseEntity} and {@link User}
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserInstancePermission> userInstancePermission = this.findFor(entity, user);

        if (userInstancePermission.isPresent()) {
            return userInstancePermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    public void setPermission(BaseEntity persistedEntity, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(persistedEntity, activeUser.get(), permissionCollectionType);
    }

    public void setPermission(BaseEntity persistedEntity, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findByName(permissionCollectionType);

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
     * Set Permission for SHOGun user for multiple entities at once
     * @param persistedEntityList A collection of entities to set permission for
     * @param user The user
     * @param permissionCollectionType The permission collection type (e.g. READ, READ_WRITE)
     */
    public void setPermission(
        List<? extends BaseEntity> persistedEntityList,
        User user,
        PermissionCollectionType permissionCollectionType
    ) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findByName(permissionCollectionType);

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

    private void clearExistingPermission(User user, PermissionCollection permissionCollection, BaseEntity entity) {
        Optional<UserInstancePermission> existingPermission = findFor(entity, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            LOG.debug("Permission is already set for entity {} and user {}: {}", entity,
                user, permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            LOG.debug("Removed the permission");
        }
    }

    public void deleteAllForEntity(BaseEntity persistedEntity) {
        List<UserInstancePermission> userInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(userInstancePermissions);

        LOG.info("Successfully deleted all user instance permissions for entity with id {}", persistedEntity.getId());
        LOG.trace("Deleted entity: {}", persistedEntity);
    }
}
