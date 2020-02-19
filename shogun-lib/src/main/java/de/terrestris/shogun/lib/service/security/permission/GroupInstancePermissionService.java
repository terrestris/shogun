package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.repository.security.permission.GroupInstancePermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.service.BaseService;
import de.terrestris.shogun.lib.service.security.IdentityService;
import de.terrestris.shogun.lib.specification.security.permission.GroupInstancePermissionSpecifications;
import de.terrestris.shogun.lib.specification.security.permission.PermissionCollectionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupInstancePermissionService extends BaseService<GroupInstancePermissionRepository, GroupInstancePermission> {

    @Autowired
    IdentityService identityService;

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    public Optional<GroupInstancePermission> findFor(BaseEntity entity, Group group) {

        LOG.trace("Getting all group permissions for group {} and entity {}", group.getName(), entity);

        return repository.findOne(Specification.where(
                GroupInstancePermissionSpecifications.hasEntity(entity)).and(
                GroupInstancePermissionSpecifications.hasGroup(group)
        ));
    }

    public Optional<GroupInstancePermission> findFor(BaseEntity entity, User user) {

        LOG.trace("Getting all group permissions for user {} and entity {}", user.getUsername(), entity);

        // Get all groups of the user
        List<Group> groups = identityService.findAllGroupsFrom(user);

        return repository.findOne(Specification.where(
                GroupInstancePermissionSpecifications.hasEntity(entity)).and(
                GroupInstancePermissionSpecifications.hasGroups(groups)
        ));
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, Group group) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, group);

        if (groupInstancePermission.isPresent()) {
            return groupInstancePermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    public PermissionCollection findPermissionCollectionFor(BaseEntity entity, User user) {
        Optional<GroupInstancePermission> groupInstancePermission = this.findFor(entity, user);

        if (groupInstancePermission.isPresent()) {
            return groupInstancePermission.get().getPermissions();
        }

        return new PermissionCollection();
    }

    public void setPermission(BaseEntity persistedEntity, Group group, PermissionCollectionType permissionCollectionType) {
        Optional<PermissionCollection> permissionCollection = permissionCollectionRepository.findOne(
            PermissionCollectionSpecification.findByName(permissionCollectionType));

        if (permissionCollection.isEmpty()) {
            throw new RuntimeException("Could not find requested permission collection");
        }

        Optional<GroupInstancePermission> existingPermissions = findFor(persistedEntity, group);

        // Check if there is already an existing permission set on the entity
        if (existingPermissions.isPresent()) {
            LOG.debug("Permission is already set for entity {} and group {}: {}", persistedEntity,
                group, permissionCollection.get());

            // Remove the existing one
            repository.delete(existingPermissions.get());

            LOG.debug("Removed the permission");
        }

        GroupInstancePermission groupInstancePermission = new GroupInstancePermission();
        groupInstancePermission.setGroup(group);
        groupInstancePermission.setEntity(persistedEntity);
        groupInstancePermission.setPermissions(permissionCollection.get());

        repository.save(groupInstancePermission);
    }
}
