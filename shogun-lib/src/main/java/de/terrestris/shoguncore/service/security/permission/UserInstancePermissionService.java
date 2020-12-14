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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserInstancePermissionService extends BaseService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all user permissions for user {} and entity {}", user.getUsername(), entity);

        return repository.findOne(Specification.where(
                UserInstancePermissionSpecifications.hasEntity(entity)).and(
                UserInstancePermissionSpecifications.hasUser(user)
        ));
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserInstancePermission> userInstancePermission = this.findFor(entity, user);

        return getPermissionCollection(userInstancePermission);
    }

    public void setPermission(BaseEntity persistedEntity, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(persistedEntity, activeUser.get(), permissionCollectionType);
    }

    public void setPermission(BaseEntity persistedEntity, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<UserInstancePermission> existingPermissions = findFor(persistedEntity, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for entity {} and user {}: {}", persistedEntity,
                user, permissionCollection.get());

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

    private PermissionCollection getPermissionCollection(Optional<UserInstancePermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }
}
