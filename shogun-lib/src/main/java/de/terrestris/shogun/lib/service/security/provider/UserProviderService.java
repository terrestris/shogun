package de.terrestris.shogun.lib.service.security.provider;

import de.terrestris.shogun.lib.model.User;

public interface UserProviderService {
    public User findOrCreateByProviderId(String keycloakUserId);

    public User setTransientRepresentations(User user);
}
