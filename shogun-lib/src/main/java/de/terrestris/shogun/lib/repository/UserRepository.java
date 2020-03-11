package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseCrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByKeycloakId(String keycloakId);
}
