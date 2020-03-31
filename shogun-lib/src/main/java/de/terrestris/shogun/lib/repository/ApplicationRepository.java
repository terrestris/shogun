package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Application;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends BaseCrudRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByName(String name);

}
