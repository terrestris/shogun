/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/roles")
@ConditionalOnExpression("${controller.roles.enabled:true}")
@Log4j2
@Tag(
    name = "Roles",
    description = "The endpoints to manage roles"
)
@SecurityRequirement(name = "bearer-key")
public class RoleController extends BaseController<RoleService, Role> {

    @PostMapping("/createAllFromProvider")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Creates all roles from the associated role provider (usually Keycloak)",
        security = { @SecurityRequirement(name = "bearer-key") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "No content: The roles were successfully created"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized: You need to provide a bearer token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden: You are not allowed to execute this operation (typically because of a " +
                "missing ADMIN role)"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error: Something internal went wrong while creating the roles"
        )
    })
    public void createAllFromProvider() {
        log.trace("Requested to create all roles from the role provider");

        try {
            service.createAllFromProvider();

            log.trace("Successfully created all roles from the role provider");
        } catch (AccessDeniedException ade) {
            log.warn("Only users with ROLE_ADMIN are allowed to create roles from the role provider");
            log.trace("Full stack trace: ", ade);

            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN
            );
        } catch (Exception e) {
            log.error("Error while creating the roles from the role provider: \n {}", e.getMessage());
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
