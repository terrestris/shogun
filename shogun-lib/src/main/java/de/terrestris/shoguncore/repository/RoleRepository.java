package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseCrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}
