package de.terrestris.shogun.lib.service.security.provider;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;

import java.util.List;

public interface GroupProviderService {
    public List<Group> findByUser(User user);

    public List<User> getGroupMembers(String keycloakId);

    public Group setTransientRepresentations(Group group);

}
