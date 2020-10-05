package de.terrestris.shogun.lib.repository;

import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends RevisionRepository<T, ID, Integer>, CrudRepository<T, ID> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<T> findAll();

}
