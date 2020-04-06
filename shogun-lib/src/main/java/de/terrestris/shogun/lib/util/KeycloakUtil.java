package de.terrestris.shogun.lib.util;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class KeycloakUtil {

    @Autowired
    protected RealmResource keycloakRealm;

    public UserResource getUserResource(User user) {
        UsersResource kcUsers = this.keycloakRealm.users();
        return kcUsers.get(user.getKeycloakId());
    }

    public GroupResource getGroupResource(Group group) {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.group(group.getKeycloakId());
    }

    public boolean addUserToGroup(User user, Group group) {
        UserResource kcUser = this.getUserResource(user);
        GroupResource kcGroup = this.getGroupResource(group);
        return kcUser.groups().add(kcGroup.toRepresentation());
    }

    public boolean addUserToGroup(User user, GroupRepresentation kcGroup) {
        UserResource kcUser = this.getUserResource(user);
        return kcUser.groups().add(kcGroup);
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
        try (Response response = parentGroupResource.subGroup(subGroup)){
            if (response.getStatusInfo().equals(Response.Status.OK)) {
                log.info("Added group " + subGroupName + " as SubGroup to " + groupName );
                return true;
            } else {
                String message = "Error adding group " + subGroupName + " as SubGroup to " + groupName + ", Error Message : " + response;
                log.error(message);
                throw new WebApplicationException(message, response);
            }
        }
    }

    public List<GroupRepresentation> getGroupByName(String groupName) {
        GroupsResource kcGroupRepresentation = this.keycloakRealm.groups();
        return kcGroupRepresentation.groups().stream()
            .filter(groupRepresentation -> StringUtils.equalsIgnoreCase(groupName, groupRepresentation.getName())).collect(Collectors.toList());
    }

    public GroupRepresentation addGroup(String groupName) throws WebApplicationException {
        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);
        try (Response response = this.keycloakRealm.groups().add(group)) {
            if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
                Response.StatusType statusInfo = response.getStatusInfo();
                response.bufferEntity();
                String body = response.readEntity(String.class);
                String message = "Create method returned status "
                    + statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201). Response body: " + body;
                log.error(message);
                throw new WebApplicationException(message, response);
            }
            return group;
        }
    }

    public RolesResource getRoles() {
        return keycloakRealm.roles();
    }

    public boolean isUserInGroup(User user, Group group) {
        UserResource kcUser = this.getUserResource(user);
        GroupResource kcGroup = this.getGroupResource(group);
        return kcUser.groups().contains(kcGroup);
    }

}
