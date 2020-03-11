package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Group;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends BaseCrudRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    Group findByKeycloakId(String keycloakId);
}
