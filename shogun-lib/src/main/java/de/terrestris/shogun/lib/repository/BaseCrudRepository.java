package de.terrestris.shogun.lib.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends RevisionRepository<T, ID, Integer>, CrudRepository<T, ID> { }
