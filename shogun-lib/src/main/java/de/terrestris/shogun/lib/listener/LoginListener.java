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
package de.terrestris.shogun.lib.listener;

import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.service.UserService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected UserService userService;

    @Autowired
    private UserProviderService userProviderService;

    @Override
    @Transactional
    public synchronized void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken)) {
            log.error("No JwtAuthenticationToken found, can not create the user.");
            return;
        }

        String keycloakUserId = KeycloakUtil.getKeycloakUserIdFromAuthentication(authentication);

        // Add missing user to shogun db
        userProviderService.findOrCreateByProviderId(keycloakUserId);
    }
}
