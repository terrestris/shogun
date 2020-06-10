package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.repository.BaseFileRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;
import java.util.UUID;

public abstract class BaseFileService<T extends BaseFileRepository<S, Long> & JpaSpecificationExecutor<S>, S extends File> extends BaseService<T, S> implements IBaseFileService<T, S> {

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    public Optional<S> findOne(UUID fileUuid) {
        return repository.findByFileUuid(fileUuid);
    }

}
