/*
 * SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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

package de.terrestris.shogun.lib.controller.security.permission;

import de.terrestris.shogun.lib.dto.PermissionCollectionTypeDto;
import de.terrestris.shogun.lib.exception.security.permission.*;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.*;
import de.terrestris.shogun.lib.service.BaseService;
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.RoleService;
import de.terrestris.shogun.lib.service.UserService;
import de.terrestris.shogun.lib.service.security.permission.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
public abstract class BasePermissionController<T extends BaseService<?, S>, S extends BaseEntity> {

    @Autowired
    protected T service;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected PublicInstancePermissionService publicInstancePermissionService;

    @Autowired
    protected UserInstancePermissionServiceSecured userInstancePermissionService;

    @Autowired
    protected UserClassPermissionServiceSecured userClassPermissionService;

    @Autowired
    protected GroupInstancePermissionServiceSecured groupInstancePermissionService;

    @Autowired
    protected GroupClassPermissionServiceSecured groupClassPermissionService;

    @Autowired
    protected RoleInstancePermissionService roleInstancePermissionService;

    @Autowired
    protected RoleClassPermissionService roleClassPermissionService;

    @GetMapping("/{id}/permissions/instance/user")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInstancePermission> getUserInstancePermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all user instance permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<UserInstancePermission> permissions = userInstancePermissionService
                .findFor(entity.get());

            log.trace("Successfully got all user instance permissions for entity " +
                "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/instance/group")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInstancePermission> getGroupInstancePermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all group instance permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<GroupInstancePermission> permissions = groupInstancePermissionService
                .findFor(entity.get());

            log.trace("Successfully got all group instance permissions for entity " +
                "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/instance/role")
    @ResponseStatus(HttpStatus.OK)
    public List<RoleInstancePermission> getRoleInstancePermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all role instance permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<RoleInstancePermission> permissions = roleInstancePermissionService
                .findFor(entity.get());

            log.trace("Successfully got all role instance permissions for entity " +
                    "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/user")
    @ResponseStatus(HttpStatus.OK)
    public List<UserClassPermission> getUserClassPermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all user class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<UserClassPermission> permissions = userClassPermissionService
                .findFor(entity.get());

            log.trace("Successfully got all user class permissions for entity " +
                "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/group")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupClassPermission> getGroupClassPermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all group class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<GroupClassPermission> permissions = groupClassPermissionService
                .findFor(entity.get());

            log.trace("Successfully got all group class permissions for entity " +
                "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/role")
    @ResponseStatus(HttpStatus.OK)
    public List<RoleClassPermission> getRoleClassPermissions(@PathVariable("id") Long entityId) {
        log.trace("Requested to get all role class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            List<RoleClassPermission> permissions = roleClassPermissionService
                .findFor(entity.get());

            log.trace("Successfully got all role class permissions for entity " +
                    "of type {} with ID {} (count: {})", getGenericClassName(), entityId,
                permissions.size());

            return permissions;
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserInstancePermission getUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        log.trace("Requested to get the user instance permission for entity of " +
            "type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            Optional<UserInstancePermission> permission = userInstancePermissionService
                .findFor(entity.get(), user.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the user instance permission for entity " +
                "of type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupInstancePermission getGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        log.trace("Requested to get the group instance permission for entity of " +
            "type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            Optional<GroupInstancePermission> permission = groupInstancePermissionService
                .findFor(entity.get(), group.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the group instance permission for entity " +
                "of type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/instance/role/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public RoleInstancePermission getRoleInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId
    ) {
        log.trace("Requested to get the role instance permission for entity of " +
            "type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            Optional<RoleInstancePermission> permission = roleInstancePermissionService
                .findFor(entity.get(), role.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the role instance permission for entity " +
                "of type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserClassPermission getUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        log.trace("Requested to get the user class permission for entity of " +
            "type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            Optional<UserClassPermission> permission = userClassPermissionService
                .findFor(entity.get().getClass(), user.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the user class permission for entity of " +
                "type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupClassPermission getGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId)
    {
        log.trace("Requested to get the group class permission for entity of " +
            "type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            Optional<GroupClassPermission> permission = groupClassPermissionService
                .findFor(entity.get().getClass(), group.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the group class permission for entity of " +
                "type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/class/role/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public RoleClassPermission getRoleClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId)
    {
        log.trace("Requested to get the role class permission for entity of " +
            "type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            Optional<RoleClassPermission> permission = roleClassPermissionService
                .findFor(entity.get().getClass(), role.get());

            if (permission.isEmpty()) {
                throw new EntityPermissionNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            log.trace("Successfully got the role class permission for entity of " +
                "type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

            return permission.get();
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new ReadPermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the user instance permission for entity of " +
            "type {} with ID {} for user with ID {} to {}", getGenericClassName(), entityId,
            userId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            userInstancePermissionService.setPermission(entity.get(), user.get(), permissionType.getPermission());

            log.trace("Successfully set the user instance permission for entity " +
                "of type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the group instance permission for entity of " +
            "type {} with ID {} for group with ID {} to {}", getGenericClassName(), entityId,
            groupId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            groupInstancePermissionService.setPermission(entity.get(), group.get(), permissionType.getPermission());

            log.trace("Successfully set the group instance permission for entity " +
                "of type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/instance/role/{roleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setRoleInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the role instance permission for entity of " +
                "type {} with ID {} for role with ID {} to {}", getGenericClassName(), entityId,
            roleId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            roleInstancePermissionService.setPermission(entity.get(), role.get(), permissionType.getPermission());

            log.trace("Successfully set the role instance permission for entity " +
                "of type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the user class permission for entity of " +
            "type {} with ID {} for user with ID {} to {}", getGenericClassName(), entityId,
            userId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            userClassPermissionService.setPermission(entity.get().getClass(),
                user.get(), permissionType.getPermission());

            log.trace("Successfully set the user class permission for entity " +
                "of type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the group class permission for entity of " +
            "type {} with ID {} for group with ID {} to {}", getGenericClassName(), entityId,
            groupId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            groupClassPermissionService.setPermission(entity.get().getClass(),
                group.get(), permissionType.getPermission());

            log.trace("Successfully set the group class permission for entity " +
                "of type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @PostMapping("/{id}/permissions/class/role/{roleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void setRoleClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId,
        @RequestBody PermissionCollectionTypeDto permissionType
    ) {
        log.trace("Requested to set the role class permission for entity of " +
                "type {} with ID {} for role with ID {} to {}", getGenericClassName(), entityId,
            roleId, permissionType.getPermission());

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            roleClassPermissionService.setPermission(entity.get().getClass(),
                role.get(), permissionType.getPermission());

            log.trace("Successfully set the role class permission for entity " +
                "of type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        log.trace("Requested to delete the user instance permission for " +
            "entity of type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            userInstancePermissionService.deleteFor(entity.get(), user.get());

            log.trace("Successfully deleted the user instance permission for " +
                "entity of type {} with ID {} for user with ID {}", getGenericClassName(),
                entityId, userId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/group/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        log.trace("Requested to delete the group instance permission for entity " +
            "of type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            groupInstancePermissionService.deleteFor(entity.get(), group.get());

            log.trace("Successfully deleted the group instance permission for " +
                "entity of type {} with ID {} for group with ID {}", getGenericClassName(),
                entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/role/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoleInstancePermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId
    ) {
        log.trace("Requested to delete the role instance permission for entity " +
            "of type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            roleInstancePermissionService.deleteFor(entity.get(), role.get());

            log.trace("Successfully deleted the role instance permission for " +
                    "entity of type {} with ID {} for role with ID {}", getGenericClassName(),
                entityId, roleId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("userId") Long userId
    ) {
        log.trace("Requested to delete the user class permission for entity of " +
            "type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<User> user = userService.findOne(userId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (user.isEmpty()) {
                throw new UserNotFoundException(userId, messageSource);
            }

            userClassPermissionService.deleteFor(entity.get(), user.get());

            log.trace("Successfully deleted the user class permission for entity " +
                "of type {} with ID {} for user with ID {}", getGenericClassName(), entityId, userId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/group/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("groupId") Long groupId
    ) {
        log.trace("Requested to delete the group class permission for entity of " +
            "type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Group> group = groupService.findOne(groupId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (group.isEmpty()) {
                throw new GroupNotFoundException(groupId, messageSource);
            }

            groupClassPermissionService.deleteFor(entity.get(), group.get());

            log.trace("Successfully deleted the group class permission for entity " +
                "of type {} with ID {} for group with ID {}", getGenericClassName(), entityId, groupId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/role/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoleClassPermission(
        @PathVariable("id") Long entityId,
        @PathVariable("roleId") Long roleId
    ) {
        log.trace("Requested to delete the role class permission for entity of " +
            "type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);

        try {
            Optional<S> entity = service.findOne(entityId);
            Optional<Role> role = roleService.findOne(roleId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            if (role.isEmpty()) {
                throw new RoleNotFoundException(roleId, messageSource);
            }

            roleClassPermissionService.deleteFor(entity.get(), role.get());

            log.trace("Successfully deleted the role class permission for entity " +
                "of type {} with ID {} for role with ID {}", getGenericClassName(), entityId, roleId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserInstancePermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all user instance permissions for entity " +
            "of type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            userInstancePermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all user instance permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/group")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupInstancePermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all group instance permissions for entity " +
            "of type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            groupInstancePermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all group instance permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/instance/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoleInstancePermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all role instance permissions for entity " +
            "of type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            roleInstancePermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all role instance permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserClassPermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all user class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            userClassPermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all user class permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/group")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupClassPermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all group class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            groupClassPermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all group instance permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/class/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoleClassPermissions(
        @PathVariable("id") Long entityId
    ) {
        log.trace("Requested to delete all role class permissions for entity of " +
            "type {} with ID {}", getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            roleClassPermissionService.deleteAllFor(entity.get());

            log.trace("Successfully deleted all role instance permissions for entity " +
                "of type {} with ID {}", getGenericClassName(), entityId);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
    }

    @GetMapping("/{id}/permissions/public")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> isPublic(
        @PathVariable("id") Long entityId
    ) {
        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            return Map.of("public", publicInstancePermissionService.getPublic(entity.get()));
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, iae.getMessage());
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (Exception e) {
            log.error("Error while checking if entity is public: {}", e.getMessage());
            log.trace("Full stack trace: ", e);
            throw e;
        }
    }

    @PostMapping("/{id}/permissions/public")
    @ResponseStatus(HttpStatus.OK)
    public void setPublic(
        @PathVariable("id") Long entityId
    ) {
        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            publicInstancePermissionService.setPublic(entity.get(), true);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        }  catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, iae.getMessage());
        } catch (Exception e) {
            throw new CreatePermissionException(e, messageSource);
        }
    }

    @DeleteMapping("/{id}/permissions/public")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokePublic(
        @PathVariable("id") Long entityId
    ) {
        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isEmpty()) {
                throw new EntityNotFoundException(entityId, getGenericClassName(), messageSource);
            }

            publicInstancePermissionService.setPublic(entity.get(), false);
        } catch (AccessDeniedException ade) {
            throw new EntityAccessDeniedException(entityId, getGenericClassName(), messageSource);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, iae.getMessage());
        } catch (Exception e) {
            throw new DeletePermissionException(e, messageSource);
        }
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
