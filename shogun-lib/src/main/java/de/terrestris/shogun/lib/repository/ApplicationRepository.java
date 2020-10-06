package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Application;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends BaseCrudRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Application> findByName(String name);

}
