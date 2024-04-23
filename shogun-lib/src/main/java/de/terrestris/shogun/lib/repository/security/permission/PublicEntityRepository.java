package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.PublicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicEntityRepository extends CrudRepository<PublicEntity, Long> {

}
