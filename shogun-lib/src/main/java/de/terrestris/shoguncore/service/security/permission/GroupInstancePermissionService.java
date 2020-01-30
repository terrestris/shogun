package de.terrestris.shoguncore.service.security.permission;

import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.GroupInstancePermission;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.repository.security.permission.GroupInstancePermissionRepository;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.service.BaseService;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.specification.security.permission.GroupInstancePermissionSpecifications;
import de.terrestris.shoguncore.specification.security.permission.PermissionCollectionSpecification;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
