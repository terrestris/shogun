package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseCrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByKeycloakId(String keycloakId);

}
