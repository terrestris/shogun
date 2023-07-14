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

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.permission.UserClassPermissionService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService extends BaseService<UserRepository, User> {

    @Autowired
    UserProviderService userProviderService;

    @Autowired
    UserClassPermissionService userClassPermissionService;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        List<User> users = repository.findAll();

        for (User user : users) {
            userProviderService.setTransientRepresentations(user);
        }

        return users;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<User> findAllBy(Specification specification) {
        List<User> users = (List<User>) repository.findAll(specification);

        for (User user : users) {
            userProviderService.setTransientRepresentations(user);
        }

        return users;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findOne(Long id) {
        Optional<User> user = repository.findById(id);

        if (user.isPresent()) {
            userProviderService.setTransientRepresentations(user.get());
        }

        return user;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<User> findByKeyCloakId(String keycloakId) {
        Optional<User> user = repository.findByAuthProviderId(keycloakId);

        if (user.isPresent()) {
            userProviderService.setTransientRepresentations(user.get());
        }

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
        Optional<User> userOptional = repository.findByAuthProviderId(keycloakUserId);
        User user = userOptional.orElse(null);

        if (user == null) {
            log.debug("User with keycloak id {} was deleted in Keycloak. It did not exists in SHOGun DB. No action needed.", keycloakUserId);
            return;
        }

        userClassPermissionService.deleteAllFor(user);
        userInstancePermissionService.deleteAllFor(user);

        repository.delete(user);

        log.info("User with keycloak id {} was deleted in Keycloak and was therefore deleted in SHOGun DB, too.", keycloakUserId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#user, 'DELETE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(User user) {
        userClassPermissionService.deleteAllFor(user);
        userInstancePermissionService.deleteAllFor(user);

        repository.delete(user);
    }

}
