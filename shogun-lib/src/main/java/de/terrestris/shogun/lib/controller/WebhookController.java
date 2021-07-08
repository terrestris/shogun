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
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @PostMapping(value = "/keycloak")
    public void handleKeyCloakEvent(@RequestBody KeycloakEventDto event) {
        Set<String> relevantResourceTypes = new HashSet(Arrays.asList(
            "GROUP_MEMBERSHIP",
            "GROUP",
            "USER"
        ));

        log.debug("Keycloak webhook called with event: {}", event);
        String resourceType = event.getResourceType();
        String eventType = event.getType();
        if (relevantResourceTypes.contains(resourceType)) {
            String resourcePath = event.getResourcePath();
            if (StringUtils.isNotEmpty(resourcePath)) {
                String[] split = resourcePath.split("/");
                if (StringUtils.equals(resourceType, "GROUP_MEMBERSHIP")) {
                    userService.findOrCreateByKeyCloakId(split[1]);
                } else if (StringUtils.equals(resourceType, "USER")) {
                    if (StringUtils.equals(eventType, "CREATE")) {
                        userService.findOrCreateByKeyCloakId(split[1]);
                    } else if (StringUtils.equals(eventType, "DELETE")) {
                        userService.deleteByKeycloakId(split[1]);
                    }
                } else if (StringUtils.equals(resourceType, "GROUP")) {
                    if (StringUtils.equals(eventType, "CREATE")) {
                        groupService.findOrCreateByKeycloakId(split[1]);
                    } else if (StringUtils.equals(eventType, "DELETE")) {
                        groupService.deleteByKeycloakId(split[1]);
                    }
                }
            }
        }
    }

}
