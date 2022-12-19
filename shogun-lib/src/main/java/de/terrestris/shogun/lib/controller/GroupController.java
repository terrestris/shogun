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
package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
@ConditionalOnExpression("${controller.groups.enabled:true}")
@Log4j2
@Tag(
    name = "Users",
    description = "The endpoints to manage users"
)
@SecurityRequirement(name = "bearer-key")
public class GroupController extends BaseController<GroupService, Group> {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Group> findByUser(@PathVariable("id") Long userId) {
        try {
            Optional<User> user = userService.findOne(userId);

            if (user.isPresent()) {
                return service.findByUser(user.get());
            } else {
                throw new Exception("Could not find user with ID " +  userId);
            }
        } catch (Exception e) {
            log.error("Error while finding groups for user with ID {}: \n {}",
                userId, e.getMessage());
            log.trace("Full stack trace: ", e);

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
    }

    // TODO Fix security issue: filter out groups the user is not allowed to see
    @GetMapping("/keycloak/{id}/members")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getGroupMembers(@PathVariable("id") String keycloakId) {
        try {
            return service.getGroupMembers(keycloakId);
        } catch (Exception e) {
            log.error("Error while requesting the members of keycloak group with ID {}: \n {}",
                keycloakId, e.getMessage());
            log.trace("Full stack trace: ", e);

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
    }

}
