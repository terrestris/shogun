package de.terrestris.shogun.lib.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends CrudRepository<T, ID> { }
