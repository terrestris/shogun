package de.terrestris.shoguncore.repository.security;

import de.terrestris.shoguncore.model.security.Identity;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends BaseCrudRepository<Identity, Long>, JpaSpecificationExecutor<Identity> {
}
