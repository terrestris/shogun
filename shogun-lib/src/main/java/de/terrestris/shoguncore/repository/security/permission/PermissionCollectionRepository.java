package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionCollectionRepository extends BaseCrudRepository<PermissionCollection, Long>, JpaSpecificationExecutor<PermissionCollection> {
}
