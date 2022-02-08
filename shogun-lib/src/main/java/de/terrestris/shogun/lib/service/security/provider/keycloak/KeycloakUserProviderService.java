/* SHOGun, https://terrestris.github.io/shogun/
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
package de.terrestris.shogun.lib.service.security.provider.keycloak;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.event.OnRegistrationConfirmedEvent;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static de.terrestris.shogun.lib.util.KeycloakUtil.getKeycloakUserIdFromAuthentication;

/**
 * NOTE: Make sure not to use services here, else the security checks will not run on them due to circular
 * references.
 */
@ConditionalOnExpression("${keycloak.enabled:true}")
@Log4j2
@Component
public class KeycloakUserProviderService implements UserProviderService {

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    GroupProviderService groupProviderService;

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    /**
     * Finds a User by the passed keycloak ID. If it does not exists in the SHOGun DB it gets created.
     *
     * The groups of the user are also checked and created if needed.
     *
     * @param keycloakUserId
     * @return
     */
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#keycloakUserId, 'CREATE')")
    @Transactional
    public User findOrCreateByProviderId(String keycloakUserId) {
        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakUserId);
        User user = userOptional.orElse(null);

        // User is not yet it SHOGun DB
        if (user == null) {
            user = new User(keycloakUserId, null, null, null);
            userRepository.save(user);

            // If the user doesn't exist, we assume it's the first login after registration.
            eventPublisher.publishEvent(new OnRegistrationConfirmedEvent(user));

            // Add admin instance permissions for the user.
            userInstancePermissionService.setPermission(user, user, PermissionCollectionType.ADMIN);

            log.info("User with keycloak id {} did not yet exist in the SHOGun DB and was therefore created.", keycloakUserId);

            this.setTransientRepresentations(user);

            return user;
        }

        List<GroupRepresentation> keycloakUserGroups = keycloakUtil.getKeycloakUserGroups(user);

        // Add missing groups to shogun db
        keycloakUserGroups
            .stream()
            .map(GroupRepresentation::getId)
            .forEach(groupProviderService::findOrCreateByProviderId);

        this.setTransientRepresentations(user);

        return user;
    }

    public User setTransientRepresentations(User user) {
        UserResource userResource = keycloakUtil.getUserResource(user);

        try {
            UserRepresentation userRepresentation = userResource.toRepresentation();
            user.setKeycloakRepresentation(userRepresentation);
        } catch (Exception e) {
            log.warn("Could not get the UserRepresentation for user with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the user is not available in Keycloak.",
                user.getId(), user.getKeycloakId());
            log.trace("Full stack trace: ", e);
        }

        return user;
    }

    @Override
    public Optional<User> getUserBySession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakUserId = getKeycloakUserIdFromAuthentication(authentication);

        if (StringUtils.isEmpty(keycloakUserId)) {
            return Optional.empty();
        }

        Optional<User> user = userRepository.findByKeycloakId(keycloakUserId);

        user.ifPresent(this::setTransientRepresentations);

        return user;
    }

    /**
     * Returns the current user object from the database.
     *
     * @param authentication
     * @return
     */
    @Override
    public Optional<User> getUserFromAuthentication(Authentication authentication) {
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof KeycloakPrincipal)) {
            return Optional.empty();
        }
        // get user info from authentication object
        String keycloakUserId = getKeycloakUserIdFromAuthentication(authentication);
        return userRepository.findByKeycloakId(keycloakUserId);
    }


}
