package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionEvaluator extends BaseEntityPermissionEvaluator<User> {

    @Autowired
    UserProviderService userProviderService;

    @Override
    public Page<User> findAll(User user, Pageable pageable, BaseCrudRepository<User, Long> repository,
        Class<User> baseEntityClass) {
        Page<User> users = super.findAll(user, pageable, repository, baseEntityClass);

        users.forEach(u -> userProviderService.setTransientRepresentations(u));

        return users;
    }
}
