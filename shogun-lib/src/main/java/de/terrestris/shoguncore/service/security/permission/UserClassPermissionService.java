package de.terrestris.shoguncore.service.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.model.security.permission.UserClassPermission;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.repository.security.permission.UserClassPermissionRepository;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.service.BaseService;
import de.terrestris.shoguncore.specification.security.permission.PermissionCollectionSpecification;
import de.terrestris.shoguncore.specification.security.permission.UserClassPermissionSpecifications;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserClassPermissionService extends BaseService<UserClassPermissionRepository, UserClassPermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Returns all {@link UserClassPermission} for the given query arguments.
     *
     * @param user The user to find the permissions for.
     * @return The permissions.
     */
    public List<UserClassPermission> findFor(User user) {

        LOG.trace("Getting all user class permissions for user with ID {}", user.getId());

        return repository.findAll(Specification.where(
            UserClassPermissionSpecifications.hasUser(user))
        );
    }

    /**
     * Returns the {@link UserClassPermission} for the given query arguments.
     *
     * @param clazz The class to find the permission for.
     * @param user The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {

        LOG.trace("Getting the user class permission for user with ID {} and entity class {}",
            user.getId(), clazz.getCanonicalName());

        return repository.findOne(Specification.where(
            UserClassPermissionSpecifications.hasEntity(clazz)).and(
            UserClassPermissionSpecifications.hasUser(user)
        ));
    }

    /**
     * Returns the {@link UserClassPermission} for the given query arguments. Hereby
     * the class of the given entity will be considered.
     *
     * @param entity The entity to find the permission for.
     * @param user The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserClassPermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all user class permissions for user with ID {} and entity class {}",
                user.getId(), entity.getClass().getCanonicalName());

        return repository.findOne(Specification.where(
                UserClassPermissionSpecifications.hasEntity(entity)).and(
                UserClassPermissionSpecifications.hasUser(user)
        ));
    }

    /**
     * Returns the {@link PermissionCollection} for the given query arguments. Hereby
     * the class of the given entity and and all groups of the given user will be considered.
     *
     * @param entity The entity to find the collection for.
     * @param user The user to find the collection for.
     * @return The collection (may be empty).
     */
    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserClassPermission> userClassPermission = this.findFor(entity, user);

        return getPermissionCollection(userClassPermission);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given class and the currently
     * logged in user.
     *
     * @param clazz The class to set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(clazz, activeUser.get(), permissionCollectionType);
    }

    /**
     * Sets the given {@link PermissionCollectionType} for the given class and user.
     *
     * @param clazz The class to find set the permission for.
     * @param user The user to find set the permission for.
     * @param permissionCollectionType The permission to set.
     */
    public void setPermission(Class<? extends BaseEntity> clazz, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<UserClassPermission> existingPermissions = findFor(clazz, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for clazz {} and user with ID {}: {}",
                clazz.getCanonicalName(), user.getId(), permissionCollection.get());

            // Remove the existing one
            repository.delete(existingPermissions.get());

            LOG.debug("Removed the permission");
        }

        UserClassPermission userClassPermission = new UserClassPermission();
        userClassPermission.setUser(user);
        userClassPermission.setClassName(clazz.getCanonicalName());
        userClassPermission.setPermissions(permissionCollection.get());

        repository.save(userClassPermission);
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
            return classPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }
}
