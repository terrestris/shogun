package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Group;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends BaseCrudRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    Optional<Group> findByKeycloakId(String keycloakId);

}
