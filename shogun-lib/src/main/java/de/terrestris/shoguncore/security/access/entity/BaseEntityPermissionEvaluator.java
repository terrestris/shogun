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

//@Component
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

        // CHECK USER INSTANCE PERMISSIONS
        PermissionCollection userPermissionCol = null;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            userPermissionCol = new PermissionCollection();
        } else {
            userPermissionCol = userInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> userInstancePermissions = userPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the user has the ADMIN permission
        if (userInstancePermissions.contains(permission) || userInstancePermissions.contains(PermissionType.ADMIN)) {
            LOG.trace("Granting " + permission + " access by user instance permissions");
            return true;
        }

        // CHECK GROUP INSTANCE PERMISSIONS
        PermissionCollection groupPermissionsCol = null;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            groupPermissionsCol = new PermissionCollection();
        } else {
            groupPermissionsCol = groupInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> groupInstancePermissions = groupPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (groupInstancePermissions.contains(permission) || groupInstancePermissions.contains(PermissionType.ADMIN)) {
            LOG.trace("Granting " + permission + " access by group instance permissions");
            return true;
        }

        // CHECK USER CLASS PERMISSIONS
        PermissionCollection userClassPermissionCol = userClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> userClassPermissions = userClassPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (userClassPermissions.contains(permission) || userClassPermissions.contains(PermissionType.ADMIN)) {
            LOG.trace("Granting " + permission + " access by user class permissions");
            return true;
        }

        // CHECK GROUP CLASS PERMISSIONS
        PermissionCollection groupClassPermissionsCol = groupClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> groupClassPermissions = groupClassPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (groupClassPermissions.contains(permission) || groupClassPermissions.contains(PermissionType.ADMIN)) {
            LOG.trace("Granting " + permission + " access by group instance permissions");
            return true;
        }

        LOG.trace("Restricting " + permission + " access on secured object '"
                + simpleClassName + "' with ID " + entity.getId());

        return false;
    }
}
