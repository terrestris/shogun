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

import de.terrestris.shogun.lib.dto.KeycloakEventDto;
import de.terrestris.shogun.lib.event.KeycloakEvent;
import de.terrestris.shogun.lib.event.KeycloakEventType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(value = "/keycloak")
    public void handleKeyCloakEvent(@RequestBody KeycloakEventDto event) {
        log.debug("Keycloak webhook called with event: {}", event);

        if (!event.getRealmId().equalsIgnoreCase("shogun")){
            log.debug("Ignoring event for realm: {}", event.getRealmId());
            return;
        }

        String resourceType = event.getResourceType();
        String eventType = event.getType();

        if (eventType.equalsIgnoreCase("REGISTER") && resourceType == null) {
            applicationEventPublisher.publishEvent(new KeycloakEvent(
                this,
                KeycloakEventType.USER_REGISTERED,
                event.getUserId()
            ));

            return;
        }

        String resourcePath = event.getResourcePath();
        if (StringUtils.isEmpty(resourcePath)) {
            return;
        }

        String[] split = resourcePath.split("/");

        switch (resourceType) {
            case "GROUP_MEMBERSHIP" -> applicationEventPublisher.publishEvent(new KeycloakEvent(
                this,
                KeycloakEventType.USER_GROUP_MEMBERSHIP_CHANGED,
                split[1],
                split[3]
            ));
            case "USER" -> {
                if (StringUtils.equals(eventType, "CREATE")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.USER_CREATED,
                        split[1]
                    ));
                } else if (StringUtils.equals(eventType, "DELETE")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.USER_DELETED,
                        split[1]
                    ));
                }
            }
            case "GROUP" -> {
                if (StringUtils.equals(eventType, "CREATE")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.GROUP_CREATED,
                        split[1]
                    ));
                } else if (StringUtils.equals(eventType, "DELETE")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.GROUP_DELETED,
                        split[1]
                    ));
                }
            }
            case "REALM_ROLE_MAPPING", "CLIENT_ROLE_MAPPING" -> {
                if (split[0].equals("users")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.USER_ROLES_CHANGED,
                        split[1]
                    ));
                } else if (split[0].equals("groups")) {
                    applicationEventPublisher.publishEvent(new KeycloakEvent(
                        this,
                        KeycloakEventType.GROUP_ROLES_CHANGED,
                        split[1]
                    ));
                }
            }
        }
    }

}
