/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.graphql.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun.lib.graphql.exception.EntityNotAvailableException;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.BaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class BaseGraphQLController<E extends BaseEntity, S extends BaseService<? extends BaseCrudRepository<E, Long>, E>> {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected S service;

    public List<E> findAll() {
        return this.service.findAll();
    }

    public Optional<E> findOne(Long id) {
        Optional<E> persistedEntity = this.service.findOne(id);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

        return persistedEntity;
    }

    public Optional<E> findOneForTime(Long id, OffsetDateTime time) {
        Optional<E> persistedEntity = this.service.findOneByTime(id, time);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

        return persistedEntity;
    }

    public Revisions<Integer, E> findRevisions(Long id) {
        Optional<E> persistedEntity = this.service.findOne(id);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

        return this.service.findRevisions(persistedEntity.get());
    }

    public Optional<Revision<Integer, E>> findRevision(Long id, Integer revId) {
        Optional<E> persistedEntity = this.service.findOne(id);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

        Optional<Revision<Integer, E>> revision = this.service.findRevision(persistedEntity.get(), revId);

        if (revision.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Revision %s of entity with ID %s is not " +
                "available", revId, id));
        }

        return revision;
    }

    public List<E> findAllByIds(List<Long> ids) {
        return this.service.findAllById(ids);
    }

    public E create(Serializable createEntity) {
        E entity = this.deserializeInput(createEntity);

        return this.service.create(entity);
    }

    public E update(Long id, Serializable updateEntity) {
        Optional<E> persistedEntity = this.service.findOne(id);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

//        updateEntity.put("id", id);

        E entity = this.deserializeInput(updateEntity);

        E updatedEntity = null;
        try {
            updatedEntity = this.service.update(id, entity);
        } catch (IOException e) {
            log.error("Error while updating entity with ID {}: {}", id, e.getMessage());
            log.trace("Full stack trace: ", e);
        }

        return updatedEntity;
    }

    public Boolean delete(Long id) {
        Optional<E> persistedEntity = this.service.findOne(id);

        if (persistedEntity.isEmpty()) {
            throw new EntityNotAvailableException(String.format("Entity with ID %s is not " +
                "available", id));
        }

        try {
            this.service.delete(persistedEntity.get());

            return true;
        } catch(Exception e) {
            return false;
        }
    }

//    /**
//     * Returns the simple class name of the {@link BaseEntity} this abstract class
//     * has been declared with, e.g. 'Application'.
//     *
//     * @return The simple class name.
//     */
//    public String getGenericSimpleClassName() {
//        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
//            BaseGraphQLController.class);
//
//        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
//            return resolvedTypeArguments[0].getSimpleName();
//        } else {
//            return null;
//        }
//    }

    private E deserializeInput(Serializable entityMap) {
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
