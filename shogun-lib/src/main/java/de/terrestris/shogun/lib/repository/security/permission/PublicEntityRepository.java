package de.terrestris.shogun.lib.repository.security.permission;

import de.terrestris.shogun.lib.model.security.permission.PublicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicEntityRepository extends CrudRepository<PublicEntity, Long> {
    void deleteByEntityId(Long entityId);

    Optional<PublicEntity> findByEntityId(Long entityId);

}
