package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.Group;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends BaseCrudRepository<Group, Long>, JpaSpecificationExecutor<Group> {
}
