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
