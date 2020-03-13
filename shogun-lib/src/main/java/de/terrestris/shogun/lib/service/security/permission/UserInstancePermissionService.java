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
import de.terrestris.shogun.lib.specification.security.permission.UserInstancePermissionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInstancePermissionService extends BaseService<UserInstancePermissionRepository, UserInstancePermission> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    public Optional<UserInstancePermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all user permissions for user {} and entity {}", user.getKeycloakId(), entity);

        return repository.findOne(Specification.where(
                UserInstancePermissionSpecifications.hasEntity(entity)).and(
                UserInstancePermissionSpecifications.hasUser(user)
        ));
    }

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
}
