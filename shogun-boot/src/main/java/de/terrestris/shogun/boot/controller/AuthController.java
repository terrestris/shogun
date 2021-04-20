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
package de.terrestris.shogun.boot.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@ConditionalOnExpression("${controller.auth.enabled:true}")
public class AuthController {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @GetMapping("/isSessionValid")
    public ResponseEntity<?> isSessionValid(Authentication authentication) {
        LOG.debug("Checking if user is logged in.");

        if (authentication != null && authentication.isAuthenticated()) {
            LOG.debug("User is logged in!");

            return ResponseEntity.ok().build();
        }

        LOG.debug("User is NOT logged in!");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
