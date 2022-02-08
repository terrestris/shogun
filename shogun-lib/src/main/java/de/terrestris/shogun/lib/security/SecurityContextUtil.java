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
package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class SecurityContextUtil {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    private UserProviderService userProviderService;

    @Transactional(readOnly = true)
    public Optional<User> getUserBySession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakUserId = SecurityContextUtil.getKeycloakUserIdFromAuthentication(authentication);

        if (StringUtils.isEmpty(keycloakUserId)) {
            return Optional.empty();
        }

        Optional<User> user = userRepository.findByKeycloakId(keycloakUserId);

        user.ifPresent(value -> userProviderService.setTransientRepresentations(value));

        return user;
    }

    /**
     *
     * @return
     */
    public List<GrantedAuthority> getGrantedAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ArrayList<>(authentication.getAuthorities());
    }

    /**
     * Returns the current user object from the database.
     *
     * @param authentication
     * @return
     */
    public Optional<User> getUserFromAuthentication(Authentication authentication) {
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof KeycloakPrincipal)) {
            return Optional.empty();
        }
        // get user info from authentication object
        String keycloakUserId = getKeycloakUserIdFromAuthentication(authentication);
        return userRepository.findByKeycloakId(keycloakUserId);
    }

    /**
     * Return keycloak user id from {@link Authentication} object
     *   - from {@link IDToken}
     *   - from {@link org.keycloak.Token}
     * @param authentication The Spring security authentication
     * @return The keycloak user id token
     */
    public static String getKeycloakUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            IDToken idToken = keycloakSecurityContext.getIdToken();
            String keycloakUserId;

            if (idToken != null) {
                keycloakUserId = idToken.getSubject();
            } else {
                AccessToken accessToken = keycloakSecurityContext.getToken();
                keycloakUserId = accessToken.getSubject();
            }

            return keycloakUserId;
        } else {
            return null;
        }
    }

    /**
     * Return if user (in session) is an admin of SHOGun-GeoServer-Interceptor microservice
     * @return true if so, false otherwise
     */
    public boolean isInterceptorAdmin() {
        List<GrantedAuthority> authorities = getGrantedAuthorities();
        return authorities.stream().anyMatch(
            grantedAuthority -> StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "INTERCEPTOR_ADMIN") ||
                StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "ADMIN")
        );
    }

}
