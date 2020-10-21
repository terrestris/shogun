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
