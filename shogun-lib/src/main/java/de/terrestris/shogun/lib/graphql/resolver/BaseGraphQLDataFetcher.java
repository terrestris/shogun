package de.terrestris.shogun.lib.graphql.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun.lib.graphql.exception.EntityNotAvailableException;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.BaseService;
import graphql.schema.DataFetcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public abstract class BaseGraphQLDataFetcher<E extends BaseEntity, S extends BaseService<? extends BaseCrudRepository<E, Long>, E>> {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected S service;

    public DataFetcher<List<E>> findAll() {
        return dataFetchingEnvironment -> this.service.findAll();
    }

    public DataFetcher<Optional<E>> findOne() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment.getArgument("id");

            Optional<E> persistedEntity = this.service.findOne(entityId.longValue());

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            return persistedEntity;
        };
    }

    public DataFetcher<Optional<E>> findOneForTime() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment.getArgument("id");
            OffsetDateTime time = dataFetchingEnvironment.getArgument("time");

            Optional<E> persistedEntity = this.service.findOneByTime(entityId.longValue(), time);

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            return persistedEntity;
        };
    }

    public DataFetcher<Revisions<Integer, E>> findRevisions() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment.getArgument("id");

            Optional<E> persistedEntity = this.service.findOne(entityId.longValue());

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            return this.service.findRevisions(persistedEntity.get());
        };
    }

    public DataFetcher<Optional<Revision<Integer, E>>> findRevision() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment.getArgument("id");
            Integer revId = dataFetchingEnvironment.getArgument("rev");

            Optional<E> persistedEntity = this.service.findOne(entityId.longValue());

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            Optional<Revision<Integer, E>> revision = this.service.findRevision(persistedEntity.get(), revId);

            if (revision.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Revision %s of entity with ID %s is not " +
                    "available", revId, entityId));
            }

            return revision;
        };
    }

    public DataFetcher<List<E>> findAllByIds() {
        return dataFetchingEnvironment -> {
            List<Integer> entityIds = dataFetchingEnvironment.getArgument("ids");

            return this.service.findAllById(entityIds.stream().map(Integer::longValue).collect(
                Collectors.toList()));
        };
    }

    public DataFetcher<E> create() {
        return dataFetchingEnvironment -> {
            LinkedHashMap<String, Object> createEntity = dataFetchingEnvironment
                .getArgument("entity");

            E entity = this.deserializeInput(createEntity);

            return this.service.create(entity);
        };
    }

    public DataFetcher<E> update() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment.getArgument("id");

            LinkedHashMap<String, Object> updateEntity = dataFetchingEnvironment
                .getArgument("entity");

            Optional<E> persistedEntity = this.service.findOne(entityId.longValue());

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            updateEntity.put("id", entityId);

            E entity = this.deserializeInput(updateEntity);

            E updatedEntity = null;
            try {
                updatedEntity = this.service.update(entityId.longValue(), entity);
            } catch (IOException e) {
                log.error("Error while updating entity with ID {}: {}", entityId, e.getMessage());
                log.trace("Full stack trace: ", e);
            }

            return updatedEntity;
        };
    }

    public DataFetcher<Boolean> delete() {
        return dataFetchingEnvironment -> {
            Integer entityId = dataFetchingEnvironment
                .getArgument("id");

            Optional<E> persistedEntity = this.service.findOne(entityId.longValue());

            if (persistedEntity.isEmpty()) {
                throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                    "available", entityId));
            }

            try {
                this.service.delete(persistedEntity.get());

                return true;
            } catch(Exception e) {
                return false;
            }

        };
    }

    /**
     * Returns the simple class name of the {@link BaseEntity} this abstract class
     * has been declared with, e.g. 'Application'.
     *
     * @return The simple class name.
     */
    public String getGenericSimpleClassName() {
        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
            BaseGraphQLDataFetcher.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[0].getSimpleName();
        } else {
            return null;
        }
    }

    private E deserializeInput(Map<String, Object> entityMap) {
        E entity = null;

        try {
            Class<E> clazz = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

            String serializedInput = objectMapper.writeValueAsString(entityMap);

            entity = objectMapper.readValue(serializedInput, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error while reading input {}", e.getMessage());
            log.trace("Full stack trace: ", e);
        }

        return entity;
    }
}
