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
package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.event.OnRegistrationConfirmedEvent;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService extends BaseService<UserRepository, User> {

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    GroupService groupService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        List<User> users = repository.findAll();

        for (User user : users) {
            this.setTransientKeycloakRepresentations(user);
        }

        return users;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAllBy(Specification specification) {
        List<User> users = (List<User>) repository.findAll(specification);

        for (User user : users) {
            this.setTransientKeycloakRepresentations(user);
        }

        return users;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findOne(Long id) {
        Optional<User> user = repository.findById(id);

        if (user.isPresent()) {
            this.setTransientKeycloakRepresentations(user.get());
        }

        return user;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<User> findByKeyCloakId(String keycloakId) {
        Optional<User> user = repository.findByKeycloakId(keycloakId);

        if (user.isPresent()) {
            this.setTransientKeycloakRepresentations(user.get());
        }

        return user;
    }

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
    public User findOrCreateByKeyCloakId(String keycloakUserId) {
        Optional<User> userOptional = repository.findByKeycloakId(keycloakUserId);
        User user = userOptional.orElse(null);

        // User is not yet it SHOGun DB
        if (user == null) {
            user = new User(keycloakUserId, null, null, null);
            repository.save(user);

            // If the user doesn't exist, we assume it's the first login after registration.
            eventPublisher.publishEvent(new OnRegistrationConfirmedEvent(user));

            // Add admin instance permissions for the user.
            userInstancePermissionService.setPermission(user, user, PermissionCollectionType.ADMIN);

            log.info("User with keycloak id {} did not yet exist in the SHOGun DB and was therefore created.", keycloakUserId);
            this.setTransientKeycloakRepresentations(user);
            return user;
        }

        List<GroupRepresentation> keycloakUserGroups = keycloakUtil.getKeycloakUserGroups(user);

        // Add missing groups to shogun db
        keycloakUserGroups
            .stream()
            .map(GroupRepresentation::getId)
            .forEach(groupService::findOrCreateByKeycloakId);

        this.setTransientKeycloakRepresentations(user);
        return user;
    }

    /**
     *  Delete a user from the SHOGun DB by its keycloak Id.
     *
     * @param keycloakUserId
     */
    @Transactional
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#keycloakUserId, 'DELETE')")
    public void deleteByKeycloakId(String keycloakUserId) {
        Optional<User> userOptional = repository.findByKeycloakId(keycloakUserId);
        User user = userOptional.orElse(null);
        if (user == null) {
            log.debug("User with keycloak id {} was deleted in Keycloak. It did not exists in SHOGun DB. No action needed.", keycloakUserId);
            return;
        }
        userInstancePermissionService.deleteAllForEntity(user);
        repository.delete(user);
        log.info("User with keycloak id {} was deleted in Keycloak and was therefore deleted in SHOGun DB, too.", keycloakUserId);
    }

    private User setTransientKeycloakRepresentations(User user) {
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

}
