package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BasePermissionRepository<T, ID> extends BaseCrudRepository<T, ID> { }
