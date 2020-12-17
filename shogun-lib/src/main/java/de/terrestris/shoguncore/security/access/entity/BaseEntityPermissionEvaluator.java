package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.service.security.permission.GroupClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shoguncore.service.security.permission.UserClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

public abstract class BaseEntityPermissionEvaluator<E extends BaseEntity> implements EntityPermissionEvaluator<E> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private IdentityService identityService;

    @Autowired
    private UserInstancePermissionService userInstancePermissionService;

    @Autowired
    private GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    private UserClassPermissionService userClassPermissionService;

    @Autowired
    private GroupClassPermissionService groupClassPermissionService;

    @Override
    public Class<E> getEntityClassName() {
        return (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseEntityPermissionEvaluator.class);
    }

    @Override
    public boolean hasPermission(User user, E entity, PermissionType permission) {

        final String simpleClassName = entity.getClass().getSimpleName();

        LOG.trace("Evaluating whether user '{}' has permission '{}' on entity '{}' with ID {}",
            user.getEmail(), permission, simpleClassName, entity.getId());

        // CHECK USER INSTANCE PERMISSIONS
        if (this.hasPermissionByUserInstancePermission(user, entity, permission)) {
            LOG.trace("Granting {} access by user instance permissions", permission);

            return true;
        }

        // CHECK GROUP INSTANCE PERMISSIONS
        if (this.hasPermissionByGroupInstancePermission(user, entity, permission)) {
            LOG.trace("Granting {} access by group instance permissions", permission);

            return true;
        }

        // CHECK USER CLASS PERMISSIONS
        if (this.hasPermissionByUserClassPermission(user, entity, permission)) {
            LOG.trace("Granting {} access by user class permissions", permission);

            return true;
        }

        // CHECK GROUP CLASS PERMISSIONS
        if (this.hasPermissionByGroupClassPermission(user, entity, permission)) {
            LOG.trace("Granting {} access by group class permissions", permission);

            return true;
        }

        LOG.trace("Restricting {} access on secured object '{}' with ID {}",
            permission, simpleClassName, entity.getId());

        return false;
    }

    public boolean hasPermissionByUserInstancePermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection userPermissionCol;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            userPermissionCol = new PermissionCollection();
        } else {
            userPermissionCol = userInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> userInstancePermissions = userPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the user has the ADMIN permission
        return userInstancePermissions.contains(permission) ||
            userInstancePermissions.contains(PermissionType.ADMIN);
    }

    public boolean hasPermissionByGroupInstancePermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection groupPermissionsCol;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            groupPermissionsCol = new PermissionCollection();
        } else {
            groupPermissionsCol = groupInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> groupInstancePermissions = groupPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        return groupInstancePermissions.contains(permission) ||
            groupInstancePermissions.contains(PermissionType.ADMIN);
    }

    public boolean hasPermissionByUserClassPermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection userClassPermissionCol = userClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> userClassPermissions = userClassPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the group has the ADMIN permission
        return userClassPermissions.contains(permission) ||
            userClassPermissions.contains(PermissionType.ADMIN);
    }

    public boolean hasPermissionByGroupClassPermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection groupClassPermissionsCol = groupClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> groupClassPermissions = groupClassPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        return groupClassPermissions.contains(permission) ||
            groupClassPermissions.contains(PermissionType.ADMIN);
    }
}
