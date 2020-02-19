package de.terrestris.shogun.lib.repository.security;

import de.terrestris.shogun.lib.model.security.Identity;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends BaseCrudRepository<Identity, Long>, JpaSpecificationExecutor<Identity> {
}
