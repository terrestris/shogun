package de.terrestris.shoguncore.security;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SecurityContextUtil {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private IdentityService identityService;

    @Transactional(readOnly = true)
    public Optional<User> getUserBySession() {
        final Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String userMail;

        if (principal instanceof String) {
            userMail = (String) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            userMail = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            return Optional.empty();
        }

        return userRepository.findOne(UserSpecification.findByMail(userMail));
    }

    @Transactional(readOnly = true)
    public Set<Role> getAllUserRoles(User user) {
        Set<Role> allUserRoles = new HashSet<>();

        // Get the roles of the user.
        // TODO Take appropriate group into account here?
        if (user != null) {
            allUserRoles.addAll(identityService.findAllRolesFrom(user));
        }

        return allUserRoles;
    }

    public List<GrantedAuthority> getGrantedAuthorities(User user) {
        Set<Role> allUserRoles = getAllUserRoles(user);

        List<GrantedAuthority> grantedAuthorities = new ArrayList();

        for (Role role : allUserRoles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return grantedAuthorities;
    }
}
