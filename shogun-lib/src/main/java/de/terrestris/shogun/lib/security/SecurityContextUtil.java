package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.specification.UserSpecification;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityContextUtil {

    @Autowired
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getUserBySession() {
        final Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String userMail;

        if (principal instanceof String) {
            userMail = (String) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            userMail = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal p = (KeycloakPrincipal) principal;
            p.getKeycloakSecurityContext();
            userMail = (String) principal;
        } else {
            return Optional.empty();
        }

        return userRepository.findOne(UserSpecification.findByMail(userMail));
    }


    public List<GrantedAuthority> getGrantedAuthorities(User user) {

        // TODO fetch from keycloak/auth context

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        return grantedAuthorities;
    }
}
