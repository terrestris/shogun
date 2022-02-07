package de.terrestris.shogun.lib.service.security.provider.keycloak;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnExpression("${keycloak.enabled:true}")
@Log4j2
@Component
public class KeycloakGroupProviderService implements GroupProviderService {

    public static final String groupUuidsClaimName = "groups_uuid";

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    GroupRepository repository;

    @Autowired
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Group> findByUser(User user) {
        List<Group> groups = new ArrayList<>();

        List<GroupRepresentation> keycloakGroups = keycloakUtil.getKeycloakUserGroups(user);

        for (GroupRepresentation keycloakGroup : keycloakGroups) {
            Optional<Group> group = repository.findByKeycloakId(keycloakGroup.getId());
            if (group.isPresent()) {
                group.get().setKeycloakRepresentation(keycloakGroup);
                groups.add(group.get());
            }
        }

        return groups;
    }

    public List<User> getGroupMembers(String id) {
        GroupResource groupResource = keycloakUtil.getGroupResource(id);
        List<UserRepresentation> groupMembers = groupResource.members();

        ArrayList<User> users = new ArrayList<>();
        for (UserRepresentation groupMember : groupMembers) {
            Optional<User> user = userRepository.findByKeycloakId(groupMember.getId());
            if (user.isPresent()) {
                user.get().setKeycloakRepresentation(groupMember);
                users.add(user.get());
            }
        }

        return users;
    }

    /**
     * Get SHOGun groups for currently logged in user based on actual assignment in keycloak
     * @return List of SHOGun for currently logged in user
     */
    public List<Group> getGroupsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof KeycloakPrincipal)) {
            // TODO Check what happens for an anon user
            return Collections.emptyList();
        }

        KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        // TODO This should happen everywhere!
        IDToken idToken = keycloakSecurityContext.getIdToken();
        AccessToken token = keycloakSecurityContext.getToken();

        ArrayList<String> idTokenGroups = (idToken == null) ? null : (ArrayList<String>) idToken.getOtherClaims().get(groupUuidsClaimName);
        ArrayList<String> tokenGroups = (token == null) ? null : (ArrayList<String>) token.getOtherClaims().get(groupUuidsClaimName);

        Set<String> keycloakGroupIds = new HashSet<>();
        if (idTokenGroups != null && !idTokenGroups.isEmpty()) {
            keycloakGroupIds.addAll(idTokenGroups);
        }

        if (tokenGroups != null && !tokenGroups.isEmpty()) {
            keycloakGroupIds.addAll(tokenGroups);
        }

        return keycloakGroupIds.stream().
            map(keycloakGroupId -> repository.findByKeycloakId(keycloakGroupId).orElseThrow()).
            collect(Collectors.toList());
    }

    public Group setTransientRepresentations(Group group) {
        GroupResource groupResource = keycloakUtil.getGroupResource(group);

        try {
            GroupRepresentation groupRepresentation = groupResource.toRepresentation();
            group.setKeycloakRepresentation(groupRepresentation);
        } catch (Exception e) {
            log.warn("Could not get the GroupRepresentation for group with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the group is not available in Keycloak.",
                group.getId(), group.getKeycloakId());
            log.trace("Full stack trace: ", e);
        }

        return group;
    }
}
