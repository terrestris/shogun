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
package de.terrestris.shogun.lib.security.access;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class BasePermissionEvaluator implements PermissionEvaluator {

    @Autowired
    protected List<BaseEntityPermissionEvaluator<?>> permissionEvaluators;

    @Autowired
    protected DefaultPermissionEvaluator defaultPermissionEvaluator;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    private UserProviderService userProviderService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject,
            Object permissionObject) {

        log.trace("About to evaluate permission for user '{}' targetDomainObject '{}' " +
            "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);

        if (authentication == null) {
            log.trace("Restricting access since no authentication is available.");
            return false;
        }

        if (targetDomainObject == null || (targetDomainObject instanceof Optional &&
            ((Optional) targetDomainObject).isEmpty())) {
            log.trace("Restricting access since no target domain object is available.");
            return false;
        }

        if (!(permissionObject instanceof String)) {
            log.trace("Restricting access since no permission object is available.");
            return false;
        }

        Optional<User> userOpt = userProviderService.getUserFromAuthentication(authentication);
        User user = userOpt.orElse(null);

        final BaseEntity persistentObject;
        if (targetDomainObject instanceof Optional) {
            persistentObject = ((Optional<BaseEntity>) targetDomainObject).get();
        } else {
            persistentObject = (BaseEntity) targetDomainObject;
        }

        final PermissionType permission = PermissionType.valueOf((String) permissionObject);

        log.trace("Getting the appropriate permission evaluator implementation for class '{}'",
            targetDomainObject.getClass().getSimpleName());

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
            this.getPermissionEvaluatorForClass(persistentObject.getClass().getCanonicalName());

        log.warn("Checking permissions with permission evaluator '{}'",
            entityPermissionEvaluator.getClass().getSimpleName());

        return entityPermissionEvaluator.hasPermission(user, persistentObject, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetDomainId,
            String targetDomainType, Object permissionObject) {

        log.trace("About to evaluate permission for user '{}' targetDomainId '{}' " +
            "of class '{}' and permissionObject '{}'", authentication, targetDomainId,
            targetDomainType, permissionObject);

        if ((authentication == null) || (targetDomainId == null) || (targetDomainType == null) ||
            !(permissionObject instanceof String)) {
            log.trace("Restricting access since not all input requirements are met.");
            return false;
        }

        Optional<User> userOpt = userProviderService.getUserFromAuthentication(authentication);
        User user = userOpt.orElse(null);

        final PermissionType permission = PermissionType.valueOf((String) permissionObject);

        long targetEntityId = Long.parseLong(String.valueOf(targetDomainId));

        log.trace("Getting the appropriate permission evaluator implementation for class '{}'",
            targetDomainType);

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
            this.getPermissionEvaluatorForClass(targetDomainType);

        log.trace("Checking permissions with permission evaluator '{}'",
            entityPermissionEvaluator.getClass().getSimpleName());

        return entityPermissionEvaluator.hasPermission(user, targetEntityId, targetDomainType, permission);
    }

    /**
     * Returns the {@BaseEntityPermissionEvaluator} for the given {@BaseEntity}.
     *
     * @return
     */
    protected BaseEntityPermissionEvaluator getPermissionEvaluatorForClass(String persistentObjectClass) {

        BaseEntityPermissionEvaluator entityPermissionEvaluator = permissionEvaluators.stream()
                .filter(permissionEvaluator -> persistentObjectClass.equals(
                        permissionEvaluator.getEntityClassName().getCanonicalName()))
                .findAny()
                .orElse(defaultPermissionEvaluator);

        return entityPermissionEvaluator;
    }
}
