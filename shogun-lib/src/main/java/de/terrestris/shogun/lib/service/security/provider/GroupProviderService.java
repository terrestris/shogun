package de.terrestris.shogun.lib.service.security.provider;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;

import java.util.List;

public interface GroupProviderService {

    List<Group> findByUser(User user);

    List<User> getGroupMembers(String keycloakId);

    void setTransientRepresentations(Group group);

    List<Group> getGroupsForUser();

}
