package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Layer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LayerRepository extends BaseCrudRepository<Layer, Long>, JpaSpecificationExecutor<Layer> {
}
