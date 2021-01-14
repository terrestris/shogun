package de.terrestris.shoguncore.graphql.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.graphql.exception.EntityNotAvailableException;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.service.BaseService;
import graphql.schema.DataFetcher;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

@Log4j2
public abstract class BaseGraphQLDataFetcher<E extends BaseEntity, S extends BaseService> {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected S service;

    public DataFetcher findAll() {
        return dataFetchingEnvironment ->  this.service.findAll();
    }

    public DataFetcher findOne() {
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

    public DataFetcher findAllByIds() {
        return dataFetchingEnvironment -> {
            List<Integer> entityIds = dataFetchingEnvironment.getArgument("ids");

            return this.service.findAllById(entityIds);
        };
    }

    public DataFetcher create() {
        return dataFetchingEnvironment -> {
            LinkedHashMap<String, Object> createEntity = dataFetchingEnvironment
                .getArgument("entity");

            E entity = this.deserializeInput(createEntity);

            E persistedEntity = (E) this.service.create(entity);

            return persistedEntity;
        };
    }

    public DataFetcher update() {
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
                updatedEntity = (E) this.service.update(entityId.longValue(), entity);
            } catch (IOException e) {
                log.error("Error while updating entity with ID {}: {}", entityId, e.getMessage());
                log.trace("Full stack trace: ", e);
            }

            return updatedEntity;
        };
    }

    public DataFetcher delete() {
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

    private E deserializeInput(Map entityMap) {
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
