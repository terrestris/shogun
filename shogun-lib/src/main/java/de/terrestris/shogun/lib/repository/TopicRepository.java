package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.Topic;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends BaseCrudRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Topic> findByTitle(String title);

}
