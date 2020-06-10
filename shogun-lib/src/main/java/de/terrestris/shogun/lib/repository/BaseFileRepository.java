package de.terrestris.shogun.lib.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseFileRepository<T, ID> extends BaseCrudRepository<T, ID> {

    Optional<T> findByFileUuid(UUID uuid);

}
