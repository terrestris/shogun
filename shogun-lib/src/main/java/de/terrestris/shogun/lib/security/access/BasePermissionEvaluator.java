package de.terrestris.shogun.lib.security.access;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class BasePermissionEvaluator implements PermissionEvaluator {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected List<BaseEntityPermissionEvaluator<?>> permissionEvaluators;

    @Autowired
    protected DefaultPermissionEvaluator defaultPermissionEvaluator;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    protected static final String ANONYMOUS_USERNAME = "ANONYMOUS";

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject,
            Object permissionObject) {
        LOG.trace("About to evaluate permission for user '{}' targetDomainObject '{}' " +
                "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);

        if (authentication == null) {
            LOG.trace("Restricting access since no authentication is available.");
            return false;
        }

        if (targetDomainObject == null || (targetDomainObject instanceof Optional &&
            ((Optional) targetDomainObject).isEmpty())) {
            LOG.trace("Restricting access since no target domain object is available.");
            return false;
        }

        if (!(permissionObject instanceof String)) {
            LOG.trace("Restricting access since no permission object is available.");
            return false;
        }

        // fetch user from securityUtil
        Optional<User> userOpt = securityContextUtil.getUserFromAuthentication(authentication);
        User user = userOpt.orElse(null);
        String accountName = user != null ? user.getKeycloakId() : ANONYMOUS_USERNAME;

        final BaseEntity persistentObject;
        if (targetDomainObject instanceof Optional) {
            persistentObject = ((Optional<BaseEntity>) targetDomainObject).get();
        } else {
            persistentObject = (BaseEntity) targetDomainObject;
        }

        final Long persistentObjectId = persistentObject.getId();
        final String persistentObjectSimpleName = targetDomainObject.getClass().getSimpleName();
        final PermissionType permission = PermissionType.valueOf((String) permissionObject);

        LOG.trace("Evaluating whether user '{}' has permission '{}' on entity '{}' with ID {}",
                accountName, permission, targetDomainObject.getClass().getSimpleName(),
                persistentObjectId);

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
                this.getPermissionEvaluatorForClass(persistentObject.getClass().getCanonicalName());

        if (entityPermissionEvaluator != null) {
            return entityPermissionEvaluator.hasPermission(user, persistentObject, permission);
        }

        LOG.warn("No permission evaluator for class {} could be found. Permission will " +
                "be restricted", persistentObjectSimpleName);

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetDomainId,
            String targetDomainType, Object permissionObject) {
        LOG.trace("About to evaluate permission for user '{}' targetDomainId '{}' " +
            "of class '{}' and permissionObject '{}'", authentication, targetDomainId, targetDomainType, permissionObject);

        if ((authentication == null) || (targetDomainId == null) || (targetDomainType == null) ||
            !(permissionObject instanceof String)) {
            LOG.trace("Restricting access since not all input requirements are met.");
            return false;
        }

        // fetch user from securityUtil
        Optional<User> userOpt = securityContextUtil.getUserFromAuthentication(authentication);
        User user = userOpt.orElse(null);
        String accountName = user != null ? user.getKeycloakId() : ANONYMOUS_USERNAME;
        final PermissionType permission = PermissionType.valueOf((String) permissionObject);

        LOG.trace("Evaluating whether user '{}' has permission '{}' on entity of class '{}' with ID {}",
            accountName, permission, targetDomainType, targetDomainId);

        long targetEntityId = Long.parseLong(String.valueOf(targetDomainId));

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
            this.getPermissionEvaluatorForClass(targetDomainType);

        if (entityPermissionEvaluator != null) {
            return entityPermissionEvaluator.hasPermission(user, targetEntityId, targetDomainType, permission);
        }

        return false;
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
