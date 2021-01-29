package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserClassPermissionRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.service.BaseService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserClassPermissionService extends BaseService<UserClassPermissionRepository, UserClassPermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Return {@link Optional} containing {@link UserClassPermission}
     * @param clazz The class that should be checked
     * @param user The user to check for
     * @return {@link Optional} containing {@link UserClassPermission}
     */
    public Optional<UserClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {
        String className = clazz.getCanonicalName();

        LOG.trace("Getting all user class permissions for user with Keycloak ID {} and " +
            "entity class {}", user.getKeycloakId(), className);

        return repository.findByUserIdAndClassName(user.getId(), className);
    }

    /**
     * Return {@link Optional} containing {@link UserClassPermission}
     * @param entity The entity whose class should be checked
     * @param user The user to check for
     * @return {@link Optional} containing {@link UserClassPermission}
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Class<? extends BaseEntity> clazz = entity.getClass();
        Optional<UserClassPermission> userClassPermission = this.findFor(clazz, user);

        if (userClassPermission.isPresent()) {
            return userClassPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    /**
     * Return {@link Optional} containing {@link UserClassPermission}
     * @param clazz The class that should be checked
     * @param permissionCollectionType The permissionCollectionType to set
     */
    public void setPermission(Class<? extends BaseEntity> clazz, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(clazz, activeUser.get(), permissionCollectionType);
    }

    /**
     * Return {@link Optional} containing {@link UserClassPermission}
     * @param clazz The class that should be set
     * @param user The user to set permissions for
     * @param permissionCollectionType The permissionCollectionType to set
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
        userClassPermission.setPermissions(permissionCollection.get());

        repository.save(userClassPermission);
    }

    private void clearExistingPermission(User user, PermissionCollection permissionCollection, Class<? extends BaseEntity> clazz) {
        Optional<UserClassPermission> existingPermission = findFor(clazz, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermission.isPresent()) {
            LOG.debug("Permission is already set for clazz {} and user with " +
                "Keycloak ID {}: {}", clazz.getCanonicalName(), user, permissionCollection);

            // Remove the existing one
            repository.delete(existingPermission.get());

            LOG.debug("Removed the permission");
        }
    }
}
