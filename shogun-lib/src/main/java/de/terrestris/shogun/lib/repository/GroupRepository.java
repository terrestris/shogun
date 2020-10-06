package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Group;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends BaseCrudRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Group> findByKeycloakId(String keycloakId);

}
