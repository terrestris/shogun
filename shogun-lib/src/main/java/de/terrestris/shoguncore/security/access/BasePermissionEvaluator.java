package de.terrestris.shoguncore.security.access;

import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shoguncore.specification.UserSpecification;
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
    protected UserRepository userRepository;

    @Autowired
    private List<BaseEntityPermissionEvaluator> permissionEvaluators;

    @Autowired
    private DefaultPermissionEvaluator defaultPermissionEvaluator;

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

        User user = this.getUserFromAuthentication(authentication);

        String accountName = user != null ? user.getUsername() : ANONYMOUS_USERNAME;

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

//    public boolean hasClassPermission(Authentication authentication, BaseEntity targetDomainObject, Object permissionObject) {
//        LOG.trace("About to evaluate permission for authentication '{}' targetDomainObject '{}' " +
//                "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);
//
//        if ((authentication == null) || (targetDomainObject == null) || !(permissionObject instanceof String)) {
//            LOG.trace("Restricting access since not all input requirements are met.");
//            return false;
//        }
//
//        User user = this.getUserFromAuthentication(authentication);
//
//        String accountName = user != null ? user.getUsername() : ANONYMOUS_USERNAME;
//
//        final PermissionType permission = PermissionType.valueOf((String) permissionObject);
//
//        LOG.trace("Evaluating whether user '{}' has permission '{}' on class '{}", accountName,
//                permission, targetDomainObject.getClass().getSimpleName());
//
//        BaseEntityPermissionEvaluator entityPermissionEvaluator =
//                this.getPermissionEvaluatorForClass(targetDomainObject);
//
//        return entityPermissionEvaluator.hasPermission(user, targetDomainObject.getClass(), permission);
//    }

    /**
     * Returns the {@BaseEntityPermissionEvaluator} for the given {@BaseEntity}.
     *
     * @param persistentObject
     * @return
     */
    private BaseEntityPermissionEvaluator getPermissionEvaluatorForClass(BaseEntity persistentObject) {

//        String persistentObjectSimpleName = persistentObject.getClass().getSimpleName();

        BaseEntityPermissionEvaluator entityPermissionEvaluator = permissionEvaluators.stream()
                .filter(permissionEvaluator -> persistentObject.getClass().equals(
                        permissionEvaluator.getEntityClassName()))
                .findAny()
                .orElse(defaultPermissionEvaluator);

        return entityPermissionEvaluator;
    }

    /**
     * Returns the current user object from the database.
     *
     * @param authentication
     * @return
     */
    private User getUserFromAuthentication(Authentication authentication) {
        final Object principal = authentication.getPrincipal();

        String userMail;

        if (principal instanceof String) {
            userMail = (String) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            userMail = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            LOG.error("Could not detect user from authentication, evaluation of permissions will fail.");
            return null;
        }

        return userRepository.findOne(UserSpecification.findByMail(userMail)).orElse(null);
    }
}
