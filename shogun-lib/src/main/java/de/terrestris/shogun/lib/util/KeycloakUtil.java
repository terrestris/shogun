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
package de.terrestris.shogun.lib.util;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.properties.KeycloakProperties;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO Check for keycloak object key length instead?
@ConditionalOnExpression("${keycloak.enabled:true}")
@Log4j2
@Component
public class KeycloakUtil {

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Autowired
    protected RealmResource keycloakRealm;

    public List<UserRepresentation> getRealmUsers() {
        return this.keycloakRealm.users().list();
    }

    public List<GroupRepresentation> getRealmGroups() {
        return this.keycloakRealm.groups().groups();
    }

    public UserResource getUserResource(User<UserRepresentation> user) {
        UsersResource kcUsers = this.keycloakRealm.users();
        return kcUsers.get(user.getAuthProviderId());
    }

    public UserResource getUserResource(String id) {
        UsersResource kcUsers = this.keycloakRealm.users();
        return kcUsers.get(id);
    }

    public List<String> getAllUserIds() {
        UsersResource kcUsers = this.keycloakRealm.users();
        return kcUsers.list().stream().map(AbstractUserRepresentation::getId).toList();
    }

    public GroupResource getGroupResource(Group<GroupRepresentation> group) {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.group(group.getAuthProviderId());
    }

    public GroupResource getGroupResource(String id) {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.group(id);
    }

    public List<String> getAllGroupIds() {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.groups().stream().map(GroupRepresentation::getId).toList();
    }

    public void addUserToGroup(User<UserRepresentation> user, Group<GroupRepresentation> group) {
        UserResource kcUser = this.getUserResource(user);
        GroupResource kcGroup = this.getGroupResource(group);

        kcUser.joinGroup(kcGroup.toRepresentation().getId());
    }

    public void addUserToGroup(User<UserRepresentation> user, GroupRepresentation kcGroup) {
        UserResource kcUser = this.getUserResource(user);

        kcUser.joinGroup(kcGroup.getId());
    }

    public GroupResource getResourceFromRepresentation(GroupRepresentation representation) {
        return this.keycloakRealm.groups().group(representation.getId());
    }

    public UserResource getResourceFromRepresentation(UserRepresentation representation) {
        return this.keycloakRealm.users().get(representation.getId());
    }

    public boolean addSubGroupToGroup(GroupRepresentation parentGroup, GroupRepresentation subGroup) {
        String subGroupName = subGroup.getName();
        String groupName = parentGroup.getName();
        GroupResource parentGroupResource = this.getResourceFromRepresentation(parentGroup);
        try (Response response = parentGroupResource.subGroup(subGroup)) {
            if (response.getStatusInfo().equals(Response.Status.NO_CONTENT)) {
                log.info("Added group " + subGroupName + " as SubGroup to " + groupName);
                return true;
            } else {
                String message = "Error adding group " + subGroupName + " as SubGroup to " + groupName +
                    ", Error Message : " + response;
                log.error(message);
                throw new WebApplicationException(message, response);
            }
        }
    }

    public List<GroupRepresentation> getGroupByName(String groupName) {
        GroupsResource kcGroupRepresentation = this.keycloakRealm.groups();
        return kcGroupRepresentation.groups().stream()
            .filter(groupRepresentation -> StringUtils.equalsIgnoreCase(groupName, groupRepresentation.getName()))
            .collect(Collectors.toList());
    }

    public Boolean existsGroup(String groupName) {
        List<GroupRepresentation> availableGroups = this.getGroupByName(groupName);

        return !availableGroups.isEmpty();
    }

    public GroupRepresentation addGroup(String groupName) throws WebApplicationException {

        List<GroupRepresentation> availableGroups = this.getGroupByName(groupName);

        if (!availableGroups.isEmpty()) {
            log.debug("Group {} already exists.", groupName);

            return availableGroups.getFirst();
        }

        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);

        try (Response response = this.keycloakRealm.groups().add(group)) {
            if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
                Response.StatusType statusInfo = response.getStatusInfo();
                response.bufferEntity();
                String body = response.readEntity(String.class);
                String message = "Create method returned status "
                    + statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() +
                    "); expected status: Created (201). Response body: " + body;
                log.error(message);

                throw new WebApplicationException(message, response);
            }

            group.setId(CreatedResponseUtil.getCreatedId(response));

            return group;
        }
    }

    public RolesResource getRoles() {
        return keycloakRealm.roles();
    }

    public RoleRepresentation getRoleRepresentation(Role<RoleRepresentation> role) {
        RoleByIdResource roleByIdResource = keycloakRealm.rolesById();

        return roleByIdResource.getRole(role.getAuthProviderId());
    }

    public boolean isUserInGroup(User<UserRepresentation> user, Group<GroupRepresentation> group) {
        UserResource kcUser = this.getUserResource(user);
        return kcUser.groups().stream()
            .anyMatch(gr -> gr.getId().equals(group.getAuthProviderId()));
    }

    /**
     * Fetch username of user from keycloak
     * @param user
     * @return
     */
    public String getUserNameFromKeycloak(User<UserRepresentation> user) {
        UsersResource users = this.keycloakRealm.users();
        UserResource kcUser = users.get(user.getAuthProviderId());
        UserRepresentation kcUserRepresentation = kcUser.toRepresentation();
        return String.format("%s %s", kcUserRepresentation.getFirstName(), kcUserRepresentation.getLastName());
    }

    /**
     * @deprecated
     * Renamed to `getKeycloakUserGroups`.
     */
    @Deprecated
    public List<GroupRepresentation> getUserGroups(User<UserRepresentation> user) {
        return this.getKeycloakUserGroups(user);
    }

    /**
     * Get the Keycloak GroupRepresentations from a user instance.
     *
     * @param user
     * @return
     */
    public List<GroupRepresentation> getKeycloakUserGroups(User<UserRepresentation> user) {
        UserResource userResource = this.getUserResource(user);
        List<GroupRepresentation> groups = new ArrayList<>();

        try {
            groups = userResource.groups();
        } catch (Exception e) {
            log.warn("Could not get the GroupRepresentations for the groups of user with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the user is not available in Keycloak.",
                     user.getId(), user.getAuthProviderId());
            log.trace("Full stack trace: ", e);
        }

        return groups;
    }

    /**
     * Get the Keycloak RoleRepresentations (for the specified client) from a user instance.
     *
     * @param user
     * @return
     */
    public List<RoleRepresentation> getKeycloakUserRoles(User<UserRepresentation> user) {
        UserResource userResource = this.getUserResource(user);
        List<RoleRepresentation> roles = new ArrayList<>();

        try {
            ClientRepresentation clientRepresentation = getClientRepresentationFromClientId();

            if (clientRepresentation == null) {
                return roles;
            }

            roles = userResource.roles().clientLevel(clientRepresentation.getId()).listEffective();
        } catch (Exception e) {
            log.warn("Could not get the RoleMappingResource for the user with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the user is not available in Keycloak.",
                     user.getId(), user.getAuthProviderId());
            log.trace("Full stack trace: ", e);
        }

        return roles;
    }

    /**
     * Returns the client representation for the client the shogun instance is configured with (see keycloak.clientId
     * in properties).
     *
     * @return
     */
    public ClientRepresentation getClientRepresentationFromClientId() {
        List<ClientRepresentation> clientRepresentations = keycloakRealm.clients().findByClientId(keycloakProperties.getClientId());

        if (clientRepresentations.size() != 1) {
            log.error("Could not find client with clientId {} in Keycloak. " +
                "Expected to find exactly one client with this clientId, but found {} clients.",
                 keycloakProperties.getClientId(), clientRepresentations.size());

            return null;
        }

        return clientRepresentations.getFirst();
    }

    /**
     * Returns the list of (client) roles for the client the shogun instance is configured with
     * (see keycloak.clientId in properties).
     *
     * @return
     */
    public List<RoleRepresentation> getClientRoles() {
        ClientRepresentation clientRepresentation = getClientRepresentationFromClientId();

        if (clientRepresentation == null) {
            return null;
        }

        return keycloakRealm.clients().get(clientRepresentation.getId()).roles().list();
    }

    public RoleRepresentation getRoleByName(String roleName) {
        ClientRepresentation clientRepresentation = getClientRepresentationFromClientId();

        if (clientRepresentation == null) {
            return null;
        }

        RoleResource roleResource = keycloakRealm.clients().get(clientRepresentation.getId()).roles().get(roleName);

        RoleRepresentation roleRepresentation = null;

        try {
            roleRepresentation = roleResource.toRepresentation();
        } catch (Exception e) {
            log.error("Could not get the RoleRepresentation for role with name {}. This may happen if " +
                "the role is not available in Keycloak.", roleName);
        }

        return roleRepresentation;
    }

    /**
     * Return keycloak user id from {@link Authentication} object
     *   - from {@link IDToken}
     *   - from {@link org.keycloak.Token}
     * @param authentication The Spring security authentication
     * @return The keycloak user id token
     */
    public static String getKeycloakUserIdFromAuthentication(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return null;
        }

        return (String) ((JwtAuthenticationToken) authentication).getTokenAttributes().get("sub");
    }

}
