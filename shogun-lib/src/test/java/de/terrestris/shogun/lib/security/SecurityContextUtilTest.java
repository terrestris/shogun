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

import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import de.terrestris.shogun.lib.service.security.provider.keycloak.KeycloakGroupProviderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityContextUtilTest {

    @Mock
    private SecurityContextUtil securityContextUtilMock;

    private SecurityContextUtil securityContextUtil = new SecurityContextUtil();

    @Mock
    private GroupService groupService;

    @Mock
    private UserProviderService userProviderService;

    @InjectMocks
    private KeycloakGroupProviderService groupProviderService;

    @AfterEach
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

        assertNotNull(groupProviderService.getGroupsForUser());
    }

}
