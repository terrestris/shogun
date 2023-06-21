package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GroupPermissionEvaluator extends BaseEntityPermissionEvaluator<Group> {

    @Autowired
    GroupProviderService groupProviderService;

    @Override
    public Page<Group> findAll(User user, Pageable pageable, BaseCrudRepository<Group, Long> repository,
        Class<Group> baseEntityClass) {
        Page<Group> groups = super.findAll(user, pageable, repository, baseEntityClass);

        groups.forEach(u -> groupProviderService.setTransientRepresentations(u));

        return groups;
    }
}
