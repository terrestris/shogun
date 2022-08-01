package de.terrestris.shogun.lib.service.security.permission.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun.lib.model.security.permission.BasePermission;
import de.terrestris.shogun.lib.repository.security.permission.BasePermissionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public abstract class BasePermissionService<T extends BasePermissionRepository<S, Long> & JpaSpecificationExecutor<S>, S extends BasePermission> {

    @Autowired
    protected T repository;

    @Autowired
    ObjectMapper objectMapper;

    @PostFilter("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<S> findAll() {
        return repository.findAll();
    }

    @PostFilter("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<S> findAllBy(Specification specification) {
        return (List<S>) repository.findAll(specification);
    }

    @PostAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Optional<S> findOne(Long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S create(S entity) {
        return repository.save(entity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S update(Long id, S entity) throws IOException {
        Optional<S> persistedEntity = repository.findById(id);

        JsonNode jsonObject = objectMapper.valueToTree(entity);
        S updatedEntity = objectMapper.readerForUpdating(persistedEntity.get()).readValue(jsonObject);

        return repository.save(updatedEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(S entity) {
        repository.delete(entity);
    }
}
