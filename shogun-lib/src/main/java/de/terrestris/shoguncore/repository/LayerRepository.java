package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.Layer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LayerRepository extends BaseCrudRepository<Layer, Long>, JpaSpecificationExecutor<Layer> {
}
