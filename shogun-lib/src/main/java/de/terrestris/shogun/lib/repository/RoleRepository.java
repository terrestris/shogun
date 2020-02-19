package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseCrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}
