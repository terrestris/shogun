package de.terrestris.shogun.lib.controller.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import de.terrestris.shogun.lib.model.security.permission.UserClassPermission;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.service.BaseService;
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.UserService;
import de.terrestris.shogun.lib.service.security.permission.GroupClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

// TODO Specify and type extension of BaseService
public abstract class BasePermissionController<T extends BaseService<?, S>, S extends BaseEntity> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected T service;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    protected UserClassPermissionService userClassPermissionService;

    @Autowired
    protected GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    protected GroupClassPermissionService groupClassPermissionService;

    /**
    @PutMapping("/{id}/permissions/class/group/{groupId}")
    @PutMapping("/{id}/permissions/class/user/{userId}")
    @PutMapping("/{id}/permissions/instance/group/{groupId}")
    @PutMapping("/{id}/permissions/instance/user/{userId}")

    @PostMapping("/{id}/permissions/class/group/{groupId}")
    @PostMapping("/{id}/permissions/class/user/{userId}")
    @PostMapping("/{id}/permissions/instance/group/{groupId}")
    @PostMapping("/{id}/permissions/instance/user/{userId}")
//    @PostMapping("/{id}/permissions/class/group")
//    @PostMapping("/{id}/permissions/class/user")
//    @PostMapping("/{id}/permissions/instance/group")
//    @PostMapping("/{id}/permissions/instance/user")

    @DeleteMapping("/{id}/permissions/class/group/{groupId}")
    @DeleteMapping("/{id}/permissions/class/user/{userId}")
    @DeleteMapping("/{id}/permissions/instance/group/{groupId}")
    @DeleteMapping("/{id}/permissions/instance/user/{userId}")
    @DeleteMapping("/{id}/permissions/class/group")
    @DeleteMapping("/{id}/permissions/class/user")
    @DeleteMapping("/{id}/permissions/instance/group")
    @DeleteMapping("/{id}/permissions/instance/user")

    @GetMapping("/{id}/permissions/class/group/{groupId}")
    @GetMapping("/{id}/permissions/class/user/{userId}")
    @GetMapping("/{id}/permissions/instance/group/{groupId}")
    @GetMapping("/{id}/permissions/instance/user/{userId}")
    @GetMapping("/{id}/permissions/class/group")
    @GetMapping("/{id}/permissions/class/user")
    @GetMapping("/{id}/permissions/instance/group")
    @GetMapping("/{id}/permissions/instance/user")
     */

    @GetMapping("/{id}/permissions/instance/user")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInstancePermission> getUserInstancePermissions(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to get all user instance permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                List<UserInstancePermission> permissions = userInstancePermissionService.findFor(entity.get());

                LOG.trace("Successfully got all user instance permissions for entity of type {} with " +
                    "ID {} (count: {})", getGenericClassName(), entityId, permissions.size());

                return permissions;
            } else {
                throw getEntityNotFoundException(entityId);
            }
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/instance/group")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInstancePermission> getGroupInstancePermissions(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to get all group instance permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                List<GroupInstancePermission> permissions = groupInstancePermissionService.findFor(entity.get());

                LOG.trace("Successfully got all group instance permissions for entity of type {} with " +
                    "ID {} (count: {})", getGenericClassName(), entityId, permissions.size());

                return permissions;
            } else {
                throw getEntityNotFoundException(entityId);
            }
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/class/user")
    @ResponseStatus(HttpStatus.OK)
    public List<UserClassPermission> getUserClassPermissions(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to get all user class permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                List<UserClassPermission> permissions = userClassPermissionService.findFor(entity.get());

                LOG.trace("Successfully got all user class permissions for entity of type {} with " +
                    "ID {} (count: {})", getGenericClassName(), entityId, permissions.size());

                return permissions;
            } else {
                throw getEntityNotFoundException(entityId);
            }
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/class/group")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupClassPermission> getGroupClassPermissions(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to get all group class permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                List<GroupClassPermission> permissions = groupClassPermissionService.findFor(entity.get());

                LOG.trace("Successfully got all group class permissions for entity of type {} with " +
                    "ID {} (count: {})", getGenericClassName(), entityId, permissions.size());

                return permissions;
            } else {
                throw getEntityNotFoundException(entityId);
            }
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserInstancePermission getUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        LOG.trace("Requested to get the user instance permission for entity of type {} with ID {} " +
            "for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            Optional<UserInstancePermission> permission = userInstancePermissionService.findFor(entity.get(), user.get());

            if (permission.isEmpty()) {
                throw getPermissionNotFoundException(entityId);
            }

            LOG.trace("Successfully got the user instance permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupInstancePermission getGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        LOG.trace("Requested to get the group instance permission for entity of type {} with ID {} " +
            "for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            Optional<GroupInstancePermission> permission = groupInstancePermissionService.findFor(entity.get(), group.get());

            if (permission.isEmpty()) {
                throw getPermissionNotFoundException(entityId);
            }

            LOG.trace("Successfully got the group instance permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserClassPermission getUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        LOG.trace("Requested to get the user class permission for entity of type {} with ID {} " +
            "for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            Optional<UserClassPermission> permission = userClassPermissionService.findFor(entity.get().getClass(), user.get());

            if (permission.isEmpty()) {
                throw getPermissionNotFoundException(entityId);
            }

            LOG.trace("Successfully got the user class permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @GetMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupClassPermission getGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId)
    {
        LOG.trace("Requested to get the group class permission for entity of type {} with ID {} " +
            "for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            Optional<GroupClassPermission> permission = groupClassPermissionService.findFor(entity.get().getClass(), group.get());

            if (permission.isEmpty()) {
                throw getPermissionNotFoundException(entityId);
            }

            LOG.trace("Successfully got the group class permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getReadException(e);
        }
    }

    @PostMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId,
        @RequestBody PermissionCollectionType permissionType
    ) {
        LOG.trace("Requested to set the user instance permission for entity of type {} with ID {} " +
            "for user with ID {} to {}", getGenericClassName(), entityId, userId, permissionType);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            userInstancePermissionService.setPermission(entity.get(), user.get(), permissionType);

            LOG.trace("Successfully set the user instance permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getCreateException(e);
        }
    }

    @PostMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId,
        @RequestBody PermissionCollectionType permissionType
    ) {
        LOG.trace("Requested to set the group instance permission for entity of type {} with ID {} " +
            "for group with ID {} to {}", getGenericClassName(), entityId, groupId, permissionType);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            groupInstancePermissionService.setPermission(entity.get(), group.get(), permissionType);

            LOG.trace("Successfully set the group instance permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getCreateException(e);
        }
    }

    @PostMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId,
        @RequestBody PermissionCollectionType permissionType
    ) {
        LOG.trace("Requested to set the user class permission for entity of type {} with ID {} " +
            "for user with ID {} to {}", getGenericClassName(), entityId, userId, permissionType);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            userClassPermissionService.setPermission(entity.get().getClass(), user.get(), permissionType);

            LOG.trace("Successfully set the user class permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getCreateException(e);
        }
    }

    @PostMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId,
        @RequestBody PermissionCollectionType permissionType
    ) {
        LOG.trace("Requested to set the group class permission for entity of type {} with ID {} " +
            "for user with ID {} to {}", getGenericClassName(), entityId, groupId, permissionType);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            groupClassPermissionService.setPermission(entity.get().getClass(), group.get(), permissionType);

            LOG.trace("Successfully set the group class permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getCreateException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        LOG.trace("Requested to delete the user instance permission for entity of type {} with ID {} " +
            "for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            userInstancePermissionService.deleteFor(entity.get(), user.get());

            LOG.trace("Successfully deleted the user instance permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        LOG.trace("Requested to delete the group instance permission for entity of type {} with ID {} " +
            "for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            groupInstancePermissionService.deleteFor(entity.get(), group.get());

            LOG.trace("Successfully deleted the group instance permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        LOG.trace("Requested to delete the user class permission for entity of type {} with ID {} " +
            "for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (user.isEmpty()) {
                throw getUserNotFoundException(userId);
            }

            userClassPermissionService.deleteFor(entity.get(), user.get());

            LOG.trace("Successfully deleted the user class permission for entity of type {} with " +
                "ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        LOG.trace("Requested to delete the group class permission for entity of type {} with ID {} " +
            "for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            if (group.isEmpty()) {
                throw getGroupNotFoundException(groupId);
            }

            groupClassPermissionService.deleteFor(entity.get(), group.get());

            LOG.trace("Successfully deleted the group class permission for entity of type {} with " +
                "ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserInstancePermissions(
        @PathVariable("id") Long entityId
    ) {
        LOG.trace("Requested to delete all user instance permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            userInstancePermissionService.deleteAllFor(entity.get());

            LOG.trace("Successfully deleted all user instance permissions for entity of type {} with ID {}",
                getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/group")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupInstancePermissions(
        @PathVariable("id") Long entityId
    ) {
        LOG.trace("Requested to delete all group instance permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            groupInstancePermissionService.deleteAllFor(entity.get());

            LOG.trace("Successfully deleted all group instance permissions for entity of type {} with ID {}",
                getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/class/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserClassPermissions(
        @PathVariable("id") Long entityId
    ) {
        LOG.trace("Requested to delete all user class permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            userClassPermissionService.deleteAllFor(entity.get());

            LOG.trace("Successfully deleted all user class permissions for entity of type {} with ID {}",
                getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    @DeleteMapping("/{id}/permissions/class/group")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupClassPermissions(
        @PathVariable("id") Long entityId
    ) {
        LOG.trace("Requested to delete all group class permissions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw getEntityNotFoundException(entityId);
            }

            groupClassPermissionService.deleteAllFor(entity.get());

            LOG.trace("Successfully deleted all group instance permissions for entity of type {} with ID {}",
                getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw getAccessDeniedException(ade);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw getDeleteException(e);
        }
    }

    protected ResponseStatusException getCreateException(Exception e) {
        LOG.error("Error while setting permission: \n {}", e.getMessage());

        throw getException(e);
    }

    protected ResponseStatusException getReadException(Exception e) {
        LOG.error("Error while requesting permission: \n {}", e.getMessage());

        throw getException(e);
    }

    protected ResponseStatusException getUpdateException(Exception e) {
        LOG.error("Error while updating permission: \n {}", e.getMessage());

        throw getException(e);
    }

    protected ResponseStatusException getDeleteException(Exception e) {
        LOG.error("Error while deleting permission: \n {}", e.getMessage());

        throw getException(e);
    }

    protected ResponseStatusException getException(Exception e) {
        LOG.trace("Full stack trace: ", e);

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            messageSource.getMessage(
                "BaseController.INTERNAL_SERVER_ERROR",
                null,
                LocaleContextHolder.getLocale()
            ),
            e
        );
    }

    protected ResponseStatusException getPermissionNotFoundException(Long entityId) {
        LOG.error("Could not find permission for entity of type {}",
            getGenericClassName(), entityId);

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }

    protected ResponseStatusException getEntityNotFoundException(Long entityId) {
        LOG.error("Could not find entity of type {} with ID {}",
            getGenericClassName(), entityId);

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }

    protected ResponseStatusException getUserNotFoundException(Long userId) {
        LOG.error("Could not find user with ID {}", userId);

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }

    protected ResponseStatusException getGroupNotFoundException(Long groupId) {
        LOG.error("Could not find group with ID {}", groupId);

        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }

    protected ResponseStatusException getAccessDeniedException(AccessDeniedException ade) {
        LOG.info("Access to entity of type {} is denied", getGenericClassName());

        return new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            ),
            ade
        );
    }

    protected String getGenericClassName() {
        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
                BasePermissionController.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1].getSimpleName();
        } else {
            return null;
        }
    }
}
