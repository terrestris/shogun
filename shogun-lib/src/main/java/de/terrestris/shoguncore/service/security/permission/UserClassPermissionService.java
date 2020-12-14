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

    public Optional<UserClassPermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all user class permissions for user {} and entity class {}",
                user.getUsername(), entity.getClass().getCanonicalName());

        return repository.findOne(Specification.where(
                UserClassPermissionSpecifications.hasEntity(entity)).and(
                UserClassPermissionSpecifications.hasUser(user)
        ));
    }

    public Optional<UserClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {

        LOG.trace("Getting all user class permissions for user {} and entity class {}",
            user.getUsername(), clazz.getCanonicalName());

        return repository.findOne(Specification.where(
            UserClassPermissionSpecifications.hasEntity(clazz)).and(
            UserClassPermissionSpecifications.hasUser(user)
        ));
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<UserClassPermission> userClassPermission = this.findFor(entity, user);

        return getPermissionCollection(userClassPermission);
    }

    public void setPermission(Class<? extends BaseEntity> clazz, PermissionCollectionType permissionCollectionType) {
        Optional<User> activeUser = securityContextUtil.getUserBySession();

        if (activeUser.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }

        setPermission(clazz, activeUser.get(), permissionCollectionType);
    }

    public void setPermission(Class<? extends BaseEntity> clazz, User user, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<UserClassPermission> existingPermissions = findFor(clazz, user);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for clazz {} and user {}: {}", clazz.getCanonicalName(),
                user, permissionCollection.get());

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

    private PermissionCollection getPermissionCollection(Optional<UserClassPermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }
}
