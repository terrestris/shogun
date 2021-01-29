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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInstancePermissionService extends BaseService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Returns the {@link UserInstancePermission} for the given query arguments.
     *
     * @param entity The entity to find the permission for.
     * @param user The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {
        LOG.trace("Getting all user permissions for user with Keycloak ID {} and " +
            "entity with ID {}", user.getKeycloakId(), entity);

        return repository.findByUserIdAndEntityId(user.getId(), entity.getId());
    }

    /**
     * Returns all {@link UserInstancePermission} for the given entity.
     *
     * @param entity entity to get user permissions for
     * @return
     */
    public List<UserInstancePermission> findFor(BaseEntity entity) {
        LOG.trace("Getting all user permissions for entity with ID {}", entity.getId());

        return repository.findByEntityId(entity.getId());
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments.
     *
     * @param entity The entity to find the collection for.
     * @param user The user to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserInstancePermission> userInstancePermission = this.findFor(entity, user);

        return getPermissionCollection(userInstancePermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given entity and the currently
     * logged in user.
     *
     * @param persistedEntity The entity to set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(BaseEntity persistedEntity, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(persistedEntity, activeUser.get(), permissionCollectionType);
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
            LOG.debug("Permission is already set for entity with ID {} and user with " +
                "Keycloak ID {}: {}", entity.getId(), user.getKeycloakId(), permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            LOG.debug("Removed the permission");
        }
    }

    /**
     * Deletes all {@link UserInstancePermission} for the given entity.
     *
     * @param persistedEntity The entity to clear the permissions for.
     */
    public void deleteAllForEntity(BaseEntity persistedEntity) {
        List<UserInstancePermission> userInstancePermissions = this.findFor(persistedEntity);

        repository.deleteAll(userInstancePermissions);

        LOG.info("Successfully deleted all user instance permissions for entity " +
            "with ID {}", persistedEntity.getId());
        LOG.trace("Deleted entity: {}", persistedEntity);
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
}
