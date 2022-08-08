/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import lombok.extern.log4j.Log4j2;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
public abstract class BaseEntityPermissionEvaluator<E extends BaseEntity> implements EntityPermissionEvaluator<E> {

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

        log.trace("Evaluating whether user with ID '{}' has permission '{}' on entity '{}' with ID {}",
            user.getId(), permission, simpleClassName, entity.getId());

        // CHECK USER INSTANCE PERMISSIONS
        if (this.hasPermissionByUserInstancePermission(user, entity, permission)) {
            log.trace("Granting {} access by user instance permissions", permission);

            return true;
        }

        // CHECK GROUP INSTANCE PERMISSIONS
        if (this.hasPermissionByGroupInstancePermission(user, entity, permission)) {
            log.trace("Granting {} access by group instance permissions", permission);

            return true;
        }

        // CHECK USER CLASS PERMISSIONS
        if (this.hasPermissionByUserClassPermission(user, entity, permission)) {
            log.trace("Granting {} access by user class permissions", permission);

            return true;
        }

        // CHECK GROUP CLASS PERMISSIONS
        if (this.hasPermissionByGroupClassPermission(user, entity, permission)) {
            log.trace("Granting {} access by group class permissions", permission);

            return true;
        }

        log.trace("Restricting {} access on secured object '{}' with ID {}",
            permission, simpleClassName, entity.getId());

        return false;
    }

    @Override
    public boolean hasPermission(User user, Long entityId, String targetDomainType, PermissionType permission) {
        log.trace("About to find the appropriate repository for target domain {}.", targetDomainType);

        if (baseCrudRepositories == null) {
            log.trace("BaseCrudRepositories is null. Permission will be restricted.");
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
            log.warn("No repository for class {} could be found. Permission will " +
                "be restricted", targetDomainType);
            return false;
        }

        Optional<E> entity = baseCrudRepository.get().findById(entityId);

        if (entity.isEmpty()) {
            log.warn("No entity for ID {} with class {} could be found. Permission will " +
                "be restricted", entityId, targetDomainType);
            return false;
        }

        log.trace("Found entity for ID {}, permission will be evaluated now…", entityId);

        return hasPermission(user, entity.get(), permission);
    }

    public boolean hasPermissionByUserInstancePermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection userPermissionCol;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            userPermissionCol = new PermissionCollection();
        } else {
            userPermissionCol = userInstancePermissionService
                .findPermissionCollectionFor(entity, user);
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
            groupPermissionsCol = groupInstancePermissionService
                .findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> groupInstancePermissions = groupPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        return groupInstancePermissions.contains(permission) ||
            groupInstancePermissions.contains(PermissionType.ADMIN);
    }

    public boolean hasPermissionByUserClassPermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection userClassPermissionCol = userClassPermissionService
            .findPermissionCollectionFor(entity, user);
        final Set<PermissionType> userClassPermissions = userClassPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the group has the ADMIN permission
        return userClassPermissions.contains(permission) ||
            userClassPermissions.contains(PermissionType.ADMIN);
    }

    public boolean hasPermissionByGroupClassPermission(User user, BaseEntity entity, PermissionType permission) {
        PermissionCollection groupClassPermissionsCol = groupClassPermissionService
            .findPermissionCollectionFor(entity, user);
        final Set<PermissionType> groupClassPermissions = groupClassPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        return groupClassPermissions.contains(permission) ||
            groupClassPermissions.contains(PermissionType.ADMIN);
    }
}
