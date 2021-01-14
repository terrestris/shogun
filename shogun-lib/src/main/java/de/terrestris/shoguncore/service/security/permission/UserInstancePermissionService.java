package de.terrestris.shoguncore.service.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.model.security.permission.UserInstancePermission;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.repository.security.permission.UserInstancePermissionRepository;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.service.BaseService;
import de.terrestris.shoguncore.specification.security.permission.PermissionCollectionSpecification;
import de.terrestris.shoguncore.specification.security.permission.UserInstancePermissionSpecifications;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserInstancePermissionService extends BaseService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    /**
     * Returns all {@link UserInstancePermission} for the given query arguments.
     *
     * entity The entity to find the permission for.
     * @return The permissions
     */
    public List<UserInstancePermission> findFor(BaseEntity entity) {

        LOG.trace("Getting all user instance permissions for entity {}", entity);

        return repository.findAll(Specification.where(
            UserInstancePermissionSpecifications.hasEntity(entity))
        );
    }

    /**
     * Returns all {@link UserInstancePermission} for the given query arguments.
     *
     * @param user The user to find the permission for.
     * @return The permissions
     */
    public List<UserInstancePermission> findFor(User user) {

        LOG.trace("Getting all user instance permissions for user {}", user);

        return repository.findAll(Specification.where(
            UserInstancePermissionSpecifications.hasUser(user))
        );
    }

    /**
     * Returns the {@link UserInstancePermission} for the given query arguments.
     *
     * @param entity The entity to find the permission for.
     * @param user The user to find the permission for.
     * @return The (optional) permission.
     */
    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all user permissions for user {} and entity {}",
            user.getUsername(), entity);

        return repository.findOne(Specification.where(
                UserInstancePermissionSpecifications.hasEntity(entity)).and(
                UserInstancePermissionSpecifications.hasUser(user)
        ));
    }

    /**
     * Returns the {@link UserInstancePermission} for the given query arguments.
     *
     * @param entity The entity to find the permission for.
     * @param permissionCollectionType The permissionCollectionType to find the permission for.
     * @return The (optional) permission.
     */
    public List<UserInstancePermission> findFor(BaseEntity entity, PermissionCollectionType permissionCollectionType) {

        LOG.trace("Getting all user permissions for entity {} and permission collection type {}",
            entity, permissionCollectionType);

        List<UserInstancePermission> result = repository
            .findByEntityAndPermissionCollectionType(entity, permissionCollectionType);

        return result;
    }

    /**
     * Returns the {@link User} that has the ADMIN permission on the given entity.
     *
     * @param entity The entity to find the owner for.
     * @return The (optional) user.
     */
    public List<User> findOwner(BaseEntity entity) {

        LOG.trace("Getting the owners of entity {}", entity);

        List<UserInstancePermission> userInstancePermission =
            this.findFor(entity, PermissionCollectionType.ADMIN);

        if (userInstancePermission.isEmpty()) {
            LOG.debug("No user instance permission candidate found.");

            return null;
        }

        List<User> owners = userInstancePermission.stream()
            .map(UserInstancePermission::getUser)
            .collect(Collectors.toList());

        return owners;
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
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<UserInstancePermission> existingPermissions = findFor(persistedEntity, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for entity {} and user {}: {}",
                persistedEntity, user, permissionCollection.get());

            // Remove the existing one
            repository.delete(existingPermissions.get());

            LOG.debug("Removed the permission");
        }

        UserInstancePermission userInstancePermission = new UserInstancePermission();
        userInstancePermission.setUser(user);
        userInstancePermission.setEntity(persistedEntity);
        userInstancePermission.setPermissions(permissionCollection.get());

        repository.save(userInstancePermission);
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
