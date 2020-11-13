package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.permission.GroupClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class BaseEntityPermissionEvaluator<E extends BaseEntity> implements EntityPermissionEvaluator<E> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    protected GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    protected UserClassPermissionService userClassPermissionService;

    @Autowired
    protected GroupClassPermissionService groupClassPermissionService;

    @Autowired
    protected List<BaseCrudRepository> baseCrudRepositories;

    @Override
    public Class<E> getEntityClassName() {
        return (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseEntityPermissionEvaluator.class);
    }

    @Override
    public boolean hasPermission(User user, E entity, PermissionType permission) {
        final String simpleClassName = entity.getClass().getSimpleName();

        // CHECK USER INSTANCE PERMISSIONS
        PermissionCollection userPermissionCol;
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

    @Override
    public boolean hasPermission(User user, Long entityId, String targetDomainType, PermissionType permission) {
        LOG.trace("About to find the appropriate repository for target domain {}.", targetDomainType);

        if (baseCrudRepositories == null) {
            LOG.trace("BaseCrudRepositories is null. Permission will be restricted.");
            return false;
        }

        // Find the matching repository for entity with the provided target domain type
        Optional<BaseCrudRepository> baseCrudRepository = baseCrudRepositories.stream()
            .filter(repository -> {
                // currently we are always proxied due to the usage of the envers revision repository implementation
                // Todo: check if repository is proxied or not
                Class<?>[] classes = AopProxyUtils.proxiedUserInterfaces(repository);
                if (classes.length > 0) {
                    return Arrays.stream(classes)
                        .anyMatch(clazz -> {
                            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(
                                clazz, BaseCrudRepository.class);
                            if (typeArguments == null) {
                                return false;
                            } else {
                                return typeArguments[0].getCanonicalName().equalsIgnoreCase(targetDomainType);
                            }
                        });
                }
                return false;
            })
            .findFirst();

        if (baseCrudRepository.isEmpty()) {
            LOG.warn("No repository for class {} could be found. Permission will " +
                "be restricted", targetDomainType);
            return false;
        }

        Optional<E> entity = baseCrudRepository.get().findById(entityId);

        if (entity.isEmpty()) {
            LOG.warn("No entity for id {} with class {} could be found. Permission will " +
                "be restricted", entityId, targetDomainType);
            return false;
        }

        LOG.trace("Found entity for id {}, permission will be evaluated now…", entityId);

        return hasPermission(user, entity.get(), permission);
    }
}
