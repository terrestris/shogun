package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.properties.KeycloakAuthProperties;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SecurityContextUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Autowired
    private KeycloakAuthProperties keycloakAuthProperties;

    @Autowired
    private RealmResource keycloakRealm;

    @Transactional(readOnly = true)
    public Optional<User> getUserBySession() {
        final Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal p = (KeycloakPrincipal) principal;
            p.getKeycloakSecurityContext();
            return userRepository.findByKeycloakId(p.getKeycloakSecurityContext().getIdToken().getSubject());
        }

        return Optional.empty();
    }


    public List<GrantedAuthority> getGrantedAuthorities(User user) {

        // TODO fetch from keycloak/auth context
        return new ArrayList<>();
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
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) authentication.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        IDToken idToken = keycloakSecurityContext.getIdToken();
        String keycloakUserId;
        if (idToken != null) {
            keycloakUserId = idToken.getSubject();
        } else {
            AccessToken accessToken = keycloakSecurityContext.getToken();
            keycloakUserId = accessToken.getSubject();
        }
        return userRepository.findByKeycloakId(keycloakUserId);
    }

    /**
     * Get (SHOGun) groups for user based on actual assignment in keycloak
     * @param user The SHOGun user
     * @return List of groups
     */
    public List<Group> getGroupsForUser(User user) {
        List<GroupRepresentation> userGroups = this.getKeycloakGroupsForUser(user);
        if (userGroups == null) {
            return null;
        }

        // return list of Groups that are in SHOGun DB
        return userGroups.stream().
            map(GroupRepresentation::getId).
            map(keycloakGroupId -> groupRepository.findByKeycloakId(keycloakGroupId).get()).
            collect(Collectors.toList());
    }

    /**
     * Return keycloak GroupRepresentaions (groups) for user
     * @param user
     * @return
     */
    public List<GroupRepresentation> getKeycloakGroupsForUser(User user) {
        UsersResource users = this.keycloakRealm.users();
        UserResource kcUser = users.get(user.getKeycloakId());
        List<GroupRepresentation> kcGroups = kcUser.groups();
        return kcUser != null ? kcGroups : null;
    }
}
