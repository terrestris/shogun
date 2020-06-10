package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService extends BaseService<GroupRepository, Group> {

    @Autowired
    KeycloakUtil keycloakUtil;

    @Autowired
    UserRepository userRepository;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Group> findAll() {
        List<Group> groups = (List<Group>) repository.findAll();

        for (Group group : groups) {
            this.setTransientKeycloakRepresentations(group);
        }

        return groups;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Group> findAllBy(Specification specification) {
        List<Group> groups = (List<Group>) repository.findAll(specification);

        for (Group group : groups) {
            this.setTransientKeycloakRepresentations(group);
        }

        return groups;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<Group> findOne(Long id) {
        Optional<Group> group = repository.findById(id);

        if (group.isPresent()) {
            group = Optional.of(this.setTransientKeycloakRepresentations(group.get()));
        }

        return group;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<Group> findByUser(User user) {
        List<Group> groups = new ArrayList<>();

        List<GroupRepresentation> keycloakGroups = keycloakUtil.getUserGroups(user);

        for (GroupRepresentation keycloakGroup : keycloakGroups) {
            Optional<Group> group = repository.findByKeycloakId(keycloakGroup.getId());
            if (group.isPresent()) {
                group.get().setKeycloakRepresentation(keycloakGroup);
                groups.add(group.get());
            }
        }

        return groups;
    }

    public GroupRepresentation findByKeycloakId(String keycloakId) {
        GroupResource groupResource = keycloakUtil.getGroupResource(keycloakId);

        return groupResource.toRepresentation();
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

    private Group setTransientKeycloakRepresentations(Group group) {
        GroupResource groupResource = keycloakUtil.getGroupResource(group);
        GroupRepresentation groupRepresentation = groupResource.toRepresentation();

        group.setKeycloakRepresentation(groupRepresentation);

        return group;
    }

}
