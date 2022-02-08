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

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import de.terrestris.shogun.lib.service.security.provider.keycloak.KeycloakGroupProviderService;
import org.junit.After;
import org.junit.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static de.terrestris.shogun.lib.service.security.provider.keycloak.KeycloakGroupProviderService.groupUuidsClaimName;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SecurityContextUtil}
 */
public class SecurityContextUtilTest {

    @Mock
    private final SecurityContextUtil securityContextUtilMock = mock(SecurityContextUtil.class);

    private final SecurityContextUtil securityContextUtil = new SecurityContextUtil();

    @Mock
    private final GroupService groupService = mock(GroupService.class);

    @Mock
    private final KeycloakGroupProviderService groupProviderService = new KeycloakGroupProviderService();

    @After
    public void logoutMockUser() {
        SecurityContextHolder.clearContext();
    }

    /**
     * @param userRoles List, Array, String, … representing the use role(s)
     */
    public void loginMockUser(String... userRoles) {
        final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (String userRole : userRoles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole));
        }

        final String testPassword = "test";
        User testUser = new User("test", testPassword , grantedAuthorities);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(testUser, testPassword, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void currentUserIsAdmin_shouldReturnTrue() {
        // prepare a user that is interceptoradmin
        loginMockUser("ADMIN");

        assertTrue(securityContextUtil.isInterceptorAdmin());
    }

    @Test
    public void currentUserIsInterceptorAdmin_shouldReturnTrue() {
        // prepare a user that is interceptoradmin
        loginMockUser("INTERCEPTOR_ADMIN");

        assertTrue(securityContextUtil.isInterceptorAdmin());
    }

    @Test
    public void currentUserIsInterceptorAdmin_shouldReturnFalse() {
        // prepare a user that is not an interceptoradmin
        loginMockUser("USER");

        assertFalse(securityContextUtil.isInterceptorAdmin());
    }

    @Test
    public void getGroupsForUser() {
        loginMockUser("USER");
        Optional<de.terrestris.shogun.lib.model.User> user = Optional.of(new de.terrestris.shogun.lib.model.User());
        when(securityContextUtilMock.getUserBySession()).thenReturn(user);
        assertNotNull(groupProviderService.getGroupsForUser());
    }

    @Test
    public void getGroupsForUser_usesClaimIfAvailable() {
        final KeycloakPrincipal<?> keycloakPrincipal = mock(KeycloakPrincipal.class);
        final IDToken idToken = mock(IDToken.class);
        final Map<String, Object> otherClaims = new HashMap<>();
        final KeycloakSecurityContext keycloakSecurityContext = mock(KeycloakSecurityContext.class);
        final KeycloakAuthenticationToken authentication = mock(KeycloakAuthenticationToken.class);
        otherClaims.put(groupUuidsClaimName, new ArrayList<>(Arrays.asList(
            "Test group 1",
            "Test group 2"
        )));

        when(idToken.getOtherClaims()).thenReturn(otherClaims);
        when(keycloakPrincipal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
        when(keycloakPrincipal.getKeycloakSecurityContext().getIdToken()).thenReturn(idToken);
        when(authentication.getPrincipal()).thenReturn(keycloakPrincipal);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.findByKeycloakId(anyString())).thenReturn(Optional.of(new Group()));

        groupProviderService.repository = groupRepository;

        List<Group> groupsForUser = groupProviderService.getGroupsForUser();
        assertNotNull(groupsForUser);
        assertEquals("Number of mocked group instances matches number of returned group instances.", 2, groupsForUser.size());
    }

}
