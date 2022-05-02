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

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NOTE: Make sure not to use services here, else the security checks will not run on them due to circular
 * references.
 */
@ConditionalOnExpression("${keycloak.enabled:true}")
@Log4j2
@Component
public class KeycloakGroupProviderService implements GroupProviderService<UserRepresentation, GroupRepresentation> {

    public static final String groupUuidsClaimName = "groups_uuid";

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    public GroupRepository repository;

    @Autowired
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Group<GroupRepresentation>> findByUser(User<UserRepresentation> user) {
        List<Group<GroupRepresentation>> groups = new ArrayList<>();

        List<GroupRepresentation> keycloakGroups = keycloakUtil.getKeycloakUserGroups(user);

        for (GroupRepresentation keycloakGroup : keycloakGroups) {
            Optional<Group<GroupRepresentation>> group = (Optional) repository.findByAuthProviderId(keycloakGroup.getId());
            if (group.isPresent()) {
                group.get().setProviderDetails(keycloakGroup);
                groups.add(group.get());
            }
        }

        return groups;
    }

    public List<User<UserRepresentation>> getGroupMembers(String id) {
        GroupResource groupResource = keycloakUtil.getGroupResource(id);
        List<UserRepresentation> groupMembers = groupResource.members();

        ArrayList<User<UserRepresentation>> users = new ArrayList<>();
        for (UserRepresentation groupMember : groupMembers) {
            Optional<User<UserRepresentation>> user = (Optional) userRepository.findByAuthProviderId(groupMember.getId());
            if (user.isPresent()) {
                user.get().setProviderDetails(groupMember);
                users.add(user.get());
            }
        }

        return users;
    }

    /**
     * Get SHOGun groups for currently logged in user based on actual assignment in keycloak
     * @return List of SHOGun for currently logged in user
     */
    public List<Group<GroupRepresentation>> getGroupsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof KeycloakPrincipal)) {
            // TODO Check what happens for an anon user
            return Collections.emptyList();
        }

        KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        // TODO This should happen everywhere!
        IDToken idToken = keycloakSecurityContext.getIdToken();
        AccessToken token = keycloakSecurityContext.getToken();

        ArrayList<String> idTokenGroups = (idToken == null) ? null : (ArrayList<String>) idToken.getOtherClaims().get(groupUuidsClaimName);
        ArrayList<String> tokenGroups = (token == null) ? null : (ArrayList<String>) token.getOtherClaims().get(groupUuidsClaimName);

        Set<String> keycloakGroupIds = new HashSet<>();
        if (idTokenGroups != null && !idTokenGroups.isEmpty()) {
            keycloakGroupIds.addAll(idTokenGroups);
        }

        if (tokenGroups != null && !tokenGroups.isEmpty()) {
            keycloakGroupIds.addAll(tokenGroups);
        }

        return (List) keycloakGroupIds.stream().
            map(keycloakGroupId -> repository.findByAuthProviderId(keycloakGroupId).orElseThrow()).
            collect(Collectors.toList());
    }

    public void setTransientRepresentations(Group<GroupRepresentation> group) {
        GroupResource groupResource = keycloakUtil.getGroupResource(group);

        try {
            GroupRepresentation groupRepresentation = groupResource.toRepresentation();
            group.setProviderDetails(groupRepresentation);
        } catch (Exception e) {
            log.warn("Could not get the GroupRepresentation for group with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the group is not available in Keycloak.",
                group.getId(), group.getAuthProviderId());
            log.trace("Full stack trace: ", e);
        }
    }

    @Transactional
    public Group<GroupRepresentation> findOrCreateByProviderId(String keycloakGroupId) {
        Optional<Group<GroupRepresentation>> groupOptional = (Optional) repository.findByAuthProviderId(keycloakGroupId);
        Group<GroupRepresentation> group = groupOptional.orElse(null);

        if (group == null) {
            group = new Group<>(keycloakGroupId, null);
            repository.save(group);

            log.info("Group with keycloak id {} did not yet exist in the SHOGun DB and was therefore created.", keycloakGroupId);
            return group;
        }

        return group;
    }

}
