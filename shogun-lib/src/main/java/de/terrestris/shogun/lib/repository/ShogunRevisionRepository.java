package de.terrestris.shogun.lib.repository;

import org.springframework.data.history.Revisions;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

@NoRepositoryBean
public interface ShogunRevisionRepository<T, ID, N extends Number & Comparable<N>> extends RevisionRepository<T, ID, N> {
    Revisions<N, T> findRevisions(ID id);
}
