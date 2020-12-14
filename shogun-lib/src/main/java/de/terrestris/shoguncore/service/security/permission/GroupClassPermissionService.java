package de.terrestris.shoguncore.service.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.GroupClassPermission;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.repository.security.permission.GroupClassPermissionRepository;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.service.BaseService;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.specification.security.permission.GroupClassPermissionSpecifications;
import de.terrestris.shoguncore.specification.security.permission.PermissionCollectionSpecification;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class GroupClassPermissionService extends BaseService<GroupClassPermissionRepository, GroupClassPermission> {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    @Autowired
    protected GroupClassPermissionRepository groupClassPermissionRepository;

    @Autowired
    IdentityService identityService;

    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group) {

        LOG.trace("Getting all group class permissions for group {} and entity class {}",
                group.getName(), entity.getClass().getCanonicalName());

        return repository.findOne(Specification.where(
                GroupClassPermissionSpecifications.hasEntity(entity)).and(
                GroupClassPermissionSpecifications.hasGroup(group)
        ));
    }

    public Optional<GroupClassPermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all group class permissions for user {} and entity {}", user.getUsername(), entity);

        // Get all groups of the user
        List<Group> groups = identityService.findAllGroupsFrom(user);

        return repository.findOne(Specification.where(
            GroupClassPermissionSpecifications.hasEntity(entity)).and(
            GroupClassPermissionSpecifications.hasGroups(groups)
        ));
    }

    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, Group group) {

        LOG.trace("Getting all group class permissions for group {} and entity class {}",
            group.getName(), clazz.getCanonicalName());

        return repository.findOne(Specification.where(
            GroupClassPermissionSpecifications.hasEntity(clazz)).and(
            GroupClassPermissionSpecifications.hasGroup(group)
        ));
    }

    public Optional<GroupClassPermission> findFor(Class<? extends BaseEntity> clazz, User user) {

        LOG.trace("Getting all group class permissions for user {} and entity class {}",
            user.getUsername(), clazz.getCanonicalName());

        // Get all groups of the user
        List<Group> groups = identityService.findAllGroupsFrom(user);

        return repository.findOne(Specification.where(
            GroupClassPermissionSpecifications.hasEntity(clazz)).and(
            GroupClassPermissionSpecifications.hasGroups(groups)
        ));
    }

    public Optional<GroupClassPermission> findFor(BaseEntity entity, Group group, User user) {

        LOG.trace("Getting all group class permissions for user {} and entity {} in the " +
            "context of group {}", user.getUsername(), entity, group);

        boolean isUserMemberInGroup = identityService.isUserMemberInGroup(user, group);

        if (!isUserMemberInGroup) {
            LOG.trace("The user is not a member of the given group, no permissions available.");

            return Optional.empty();
        }

        return repository.findOne(Specification.where(
            GroupClassPermissionSpecifications.hasEntity(entity)).and(
            GroupClassPermissionSpecifications.hasGroup(group)
        ));
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(entity, group);

        return getPermissionCollection(groupClassPermission);
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(entity, user);

        return getPermissionCollection(groupClassPermission);
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group, User user) {
        Optional<GroupClassPermission> groupClassPermission = this.findFor(entity, group, user);

        return getPermissionCollection(groupClassPermission);
    }

    public void setPermission(Class<? extends BaseEntity> clazz, Group group, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<GroupClassPermission> existingPermissions = findFor(clazz, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for class {} and group {}: {}", clazz,
                group, permissionCollection.get());

            // Remove the existing one
            repository.delete(existingPermissions.get());

            LOG.debug("Removed the permission");
        }

        GroupClassPermission groupClassPermission = new GroupClassPermission();
        groupClassPermission.setGroup(group);
        groupClassPermission.setClassName(clazz.getCanonicalName());
        groupClassPermission.setPermissions(permissionCollection.get());

        repository.save(groupClassPermission);
    }

    private PermissionCollection getPermissionCollection(Optional<GroupClassPermission> classPermission) {
        if (classPermission.isPresent()) {
            return classPermission.get().getPermissions();
        }

        return new PermissionCollection();
    }
}
