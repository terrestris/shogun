package de.terrestris.shogun.lib.repository;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseFileRepository<T, ID> extends BaseCrudRepository<T, ID> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<T> findByFileUuid(UUID uuid);

}
