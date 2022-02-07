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

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class GroupService extends BaseService<GroupRepository, Group> {

//    @Autowired
//    KeycloakUtil keycloakUtil;

    @Autowired
    GroupProviderService groupProviderService;

    @Autowired
    UserRepository userRepository;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Group> findAll() {
        List<Group> groups = (List<Group>) repository.findAll();

        for (Group group : groups) {
            groupProviderService.setTransientRepresentations(group);
        }

        return groups;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Group> findAllBy(Specification specification) {
        List<Group> groups = (List<Group>) repository.findAll(specification);

        for (Group group : groups) {
            groupProviderService.setTransientRepresentations(group);
        }

        return groups;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<Group> findOne(Long id) {
        Optional<Group> group = repository.findById(id);

        if (group.isPresent()) {
            groupProviderService.setTransientRepresentations(group.get());
        }

        return group;
    }

    /**
     * Finds a Group by the passed keycloak ID. If it does not exists in the SHOGun DB it gets created.
     *
     * @param keycloakGroupId
     * @return
     */
    @Transactional
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#keycloakGroupId, 'CREATE')")
    public Group findOrCreateByKeycloakId(String keycloakGroupId) {
        Optional<Group> groupOptional = repository.findByKeycloakId(keycloakGroupId);
        Group group = groupOptional.orElse(null);

        if (group == null) {
            group = new Group(keycloakGroupId, null);
            repository.save(group);

            log.info("Group with keycloak id {} did not yet exist in the SHOGun DB and was therefore created.", keycloakGroupId);
            return group;
        }

        return group;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<Group> findByUser(User user) {
        return groupProviderService.findByUser(user);
    }

    /**
     *  Delete a group from the SHOGun DB by its keycloak Id.
     *
     * @param keycloakGroupId
     */
    @Transactional
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#keycloakGroupId, 'DELETE')")
    public void deleteByKeycloakId(String keycloakGroupId) {
        Optional<Group> groupOptional = repository.findByKeycloakId(keycloakGroupId);
        Group group = groupOptional.orElse(null);
        if (group == null) {
            log.debug("Group with keycloak id {} was deleted in Keycloak. It did not exists in SHOGun DB. No action needed.", keycloakGroupId);
            return;
        }
        repository.delete(group);
        log.info("Group with keycloak id {} was deleted in Keycloak and was therefore deleted in SHOGun DB, too.", keycloakGroupId);
    }

}
