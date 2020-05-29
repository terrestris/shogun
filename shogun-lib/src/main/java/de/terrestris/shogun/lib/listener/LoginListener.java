package de.terrestris.shogun.lib.listener;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Override
    @Transactional
    public synchronized void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof KeycloakPrincipal)) {
            log.error("No KeycloakPrincipal found, can not create the user.");
            return;
        }

        String keycloakUserId = SecurityContextUtil.getKeycloakUserIdFromAuthentication(authentication);

        // add missing user to shogun db
        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakUserId);
        User user = userOptional.orElse(null);
        if (user == null) {
            user = new User(keycloakUserId, null, null, null);
            userRepository.save(user);
        }

        List<GroupRepresentation> userGroups = securityContextUtil.getKeycloakGroupsForUser(user);

        // add missing groups to shogun db
        userGroups.stream().map(GroupRepresentation::getId).forEach(keycloakGroupId -> {
            Optional<Group> group = groupRepository.findByKeycloakId(keycloakGroupId);
            if (group.isEmpty()) {
                Group newGroup = new Group(keycloakGroupId);
                groupRepository.save(newGroup);
            }
        });
    }
}
