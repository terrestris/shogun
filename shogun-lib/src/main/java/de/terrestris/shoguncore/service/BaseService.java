package de.terrestris.shoguncore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends BaseCrudRepository<S, Long> & JpaSpecificationExecutor<S>, S extends BaseEntity> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected T repository;

    @Autowired
    ObjectMapper objectMapper;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<S> findAll() {
        return (List<S>) repository.findAll();
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<S> findOne(Long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'CREATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S create(S entity) {
        return repository.save(entity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S update(Long id, S entity) throws IOException {
        Optional<S> persistedEntity = repository.findById(id);

        JsonNode jsonObject = objectMapper.valueToTree(entity);
        S updatedEntity = objectMapper.readerForUpdating(persistedEntity.get()).readValue(jsonObject);

        return repository.save(updatedEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'DELETE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(S entity) {
        repository.delete(entity);
    }
}
