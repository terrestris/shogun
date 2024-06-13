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

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.UserService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@ConditionalOnExpression("${controller.users.enabled:true}")
@Log4j2
@Tag(
    name = "Users",
    description = "The endpoints to manage users"
)
@SecurityRequirement(name = "bearer-key")
public class UserController extends BaseController<UserService, User> {

    @Autowired
    private UserProviderService userProviderService;

    @PostMapping("/createFromProvider")
    @ResponseStatus(HttpStatus.OK)
    public void createFromProvider() {
        userProviderService.createAllUsers();
    }
}
