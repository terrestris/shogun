package de.terrestris.shogun.lib.security.access;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class BasePermissionEvaluator implements PermissionEvaluator {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected List<BaseEntityPermissionEvaluator<?>> permissionEvaluators;

    @Autowired
    protected DefaultPermissionEvaluator defaultPermissionEvaluator;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    private static final String ANONYMOUS_USERNAME = "ANONYMOUS";

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permissionObject) {
        LOG.trace("About to evaluate permission for user '{}' targetDomainObject '{}' " +
                "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);

        if ((authentication == null) || (targetDomainObject == null) || !(permissionObject instanceof String) ||
                (targetDomainObject instanceof Optional && ((Optional) targetDomainObject).isEmpty())) {
            LOG.trace("Restricting access since not all input requirements are met.");
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
                accountName, permission, targetDomainObject.getClass().getSimpleName(), persistentObjectId);

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
                this.getPermissionEvaluatorForClass(persistentObject);

        if (entityPermissionEvaluator != null) {
            return entityPermissionEvaluator.hasPermission(user, persistentObject, permission);
        }

        LOG.warn("No permission evaluator for class {} could be found. Permission will " +
                "be restricted", persistentObjectSimpleName);

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        LOG.trace("HUHUHUHUHUHUHUUHUH");
        return false;
    }

    /**
     * Returns the {@BaseEntityPermissionEvaluator} for the given {@BaseEntity}.
     *
     * @param persistentObject
     * @return
     */
    private BaseEntityPermissionEvaluator getPermissionEvaluatorForClass(BaseEntity persistentObject) {

        BaseEntityPermissionEvaluator entityPermissionEvaluator = permissionEvaluators.stream()
                .filter(permissionEvaluator -> persistentObject.getClass().equals(
                        permissionEvaluator.getEntityClassName()))
                .findAny()
                .orElse(defaultPermissionEvaluator);

        return entityPermissionEvaluator;
    }
}
