package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.User;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseCrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<User> findByKeycloakId(String keycloakId);

}
