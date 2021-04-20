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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for {@link SecurityContextUtil}
 */
public class SecurityContextUtilTest {

    private SecurityContextUtil securityContextUtil;

    @Before
    public void init() {
        this.securityContextUtil = new SecurityContextUtil();
    }

    @After
    public void logoutMockUser() {
        SecurityContextHolder.clearContext();
    }

    /**
     * @param userRoles
     */
    public void loginMockUser(Set<String> userRoles) {
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
        HashSet<String> rolesOfUser = new HashSet<>();
        rolesOfUser.add("ADMIN");

        loginMockUser(rolesOfUser);

        assertTrue(securityContextUtil.isInterceptorAdmin());
    }

    @Test
    public void currentUserIsInterceptorAdmin_shouldReturnTrue() {
        // prepare a user that is interceptoradmin
        HashSet<String> rolesOfUser = new HashSet<>();
        rolesOfUser.add("INTERCEPTOR_ADMIN");

        loginMockUser(rolesOfUser);

        assertTrue(securityContextUtil.isInterceptorAdmin());
    }

    @Test
    public void currentUserIsInterceptorAdmin_shouldReturnFalse() {
        // prepare a user that is interceptoradmin
        HashSet<String> rolesOfUser = new HashSet<>();
        rolesOfUser.add("USER");

        loginMockUser(rolesOfUser);

        assertFalse(securityContextUtil.isInterceptorAdmin());
    }

}
