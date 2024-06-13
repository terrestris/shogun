/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.RoleRepository;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * NOTE: Make sure not to use services here, else the security checks will not run on them due to circular
 * references.
 */
@ConditionalOnExpression("${keycloak.enabled:true}")
@Log4j2
@Component
public class KeycloakRoleProviderService implements RoleProviderService<RoleRepresentation, UserRepresentation> {

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void setTransientRepresentations(Role<RoleRepresentation> role) {
        try {
            RoleRepresentation roleRepresentation = keycloakUtil.getRoleRepresentation(role);
            role.setProviderDetails(roleRepresentation);
        } catch (Exception e) {
            log.warn("Could not get the RoleRepresentation for role with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the role is not available in Keycloak.",
                role.getId(), role.getAuthProviderId());
            log.trace("Full stack trace: ", e);
        }
    }

    @Override
    public List<Role> getRolesForUser(User<UserRepresentation> user) {
        List<Role> roles = new ArrayList<>();

        try {
            List<RoleRepresentation> rolesA = keycloakUtil.getKeycloakUserRoles(user);

            roles = rolesA.stream()
                .map(role -> roleRepository.findByAuthProviderId(role.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        } catch (Exception e) {
            log.error("Error while fetching roles for user with SHOGun ID {} and Keycloak ID {}",
                user.getId(), user.getAuthProviderId());
            log.trace("Full stack trace: ", e);
        }

        return roles;
    }

    @Override
    public Role<RoleRepresentation> findOrCreateByProviderId(String providerRoleId) {
        Optional<Role<RoleRepresentation>> roleOptional = (Optional) roleRepository.findByAuthProviderId(providerRoleId);
        Role<RoleRepresentation> role = roleOptional.orElse(null);

        if (role == null) {
            role = new Role<RoleRepresentation>(providerRoleId, null);
            roleRepository.save(role);

            log.info("Role with Keycloak ID {} did not yet exist in the SHOGun DB and was therefore created.", providerRoleId);
            return role;
        }

        return role;
    }
}
