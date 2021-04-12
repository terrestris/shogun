package de.terrestris.shogun.lib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import javax.persistence.QueryHint;
import java.util.List;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends RevisionRepository<T, ID, Integer>, PagingAndSortingRepository<T, ID> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<T> findAll();

    @Query(nativeQuery = true, value = "select m from shogun.#{#entityName} m where has_permission( m.id, ?#{ principal?.getKeycloakSecurityContext().token.subject }, ?#{ principal?.getKeycloakSecurityContext().token.otherClaims.get(\"groups_uuid\") }, 'de.terrestris.shogun.model.Application' )")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Page<T> findAll(Pageable pageable);

}
