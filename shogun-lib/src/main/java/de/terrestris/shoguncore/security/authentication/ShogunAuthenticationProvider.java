package de.terrestris.shoguncore.security.authentication;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.specification.UserSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class ShogunAuthenticationProvider implements AuthenticationProvider {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdentityService identityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // Prepare an exception
        final String exceptionMessage = "User and password do not match.";

        String email = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        LOG.debug("Trying to authenticate User with email '" + email + "'");

        if (StringUtils.isEmpty(rawPassword)) {
            LOG.warn("No password for authentication provided.");
            throw new InsufficientAuthenticationException("No password for user");
        }

        Optional<User> userCandidate = userRepository.findOne(UserSpecification.findByMail(email));

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        String encryptedPassword;

        if (!userCandidate.isPresent()) {
            LOG.warn("No user for email '" + email + "' could be found.");
            throw new UsernameNotFoundException(exceptionMessage);
        } else if (!userCandidate.get().isEnabled()) {
            LOG.warn("The user with the email '" + email + "' is not active.");
            throw new DisabledException(exceptionMessage);
        } else {
            User user = userCandidate.get();
            encryptedPassword = user.getPassword();

            // Check if rawPassword matches the hash from the DB.
            if (passwordEncoder.matches(rawPassword, encryptedPassword)) {
                // TODO Move this to ShogunUserDetailsService and make use of service to get user here?!
                Set<Role> allUserRoles = getAllUserRoles(user);

                // Create granted authorities for the security context.
                for (Role role : allUserRoles) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
                }

                user.setLastLogin(OffsetDateTime.now());
                userRepository.save(user);
            } else {
                LOG.warn("The given password for the user with email '" + email + "' does not match.");
                throw new BadCredentialsException(exceptionMessage);
            }
        }

        // Create the corresponding token to forward in Spring Security's filter
        // chain. We will use the SHOGun-Core user as the principal.
        Authentication authResult;
        if (grantedAuthorities.isEmpty()) {
            // If the user has no authorities, we will build the
            // UsernamePasswordAuthenticationToken without authorities, which
            // leads to an unauthenticated user, i.e. isAuthenticated() of
            // authenticationToken will return false afterwards.
            LOG.warn("The user with email '" + email + "' has no authorities and will thereby NOT be authenticated.");
            authResult = new UsernamePasswordAuthenticationToken(email, encryptedPassword);
        } else {
            // If we pass some grantedAuthorities, isAuthenticated() of
            // authenticationToken will return true afterwards
            authResult = new UsernamePasswordAuthenticationToken(email, encryptedPassword, grantedAuthorities);

            LOG.debug("The user with email '" + email
                    + "' got the following (explicit) roles: "
                    + StringUtils.arrayToCommaDelimitedString(getRawRoleNames(grantedAuthorities)));
        }

        final boolean isAuthenticated = authResult.isAuthenticated();
        final String authLog = isAuthenticated ? "has succesfully" : "has NOT";

        LOG.info("The user with email '" + email + "' " + authLog + " been authenticated.");

        return authResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(aClass));
    }

    private Set<Role> getAllUserRoles(User user) {
        Set<Role> allUserRoles = new HashSet<>();

        // Get the roles of the user.
        // TODO Take appropriate group into account here?
        if (user != null) {
            allUserRoles.addAll(identityService.findAllRolesFrom(user));
        }

        return allUserRoles;
    }

    private String[] getRawRoleNames(Set<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream().map(
                auth -> auth.getAuthority()).toArray(String[]::new);
    }
}
