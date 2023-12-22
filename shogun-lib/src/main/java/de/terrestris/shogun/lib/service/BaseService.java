/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shogun.lib.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class BaseService<T extends BaseCrudRepository<S, Long> & JpaSpecificationExecutor<S>, S extends BaseEntity> {

    @Autowired
    protected T repository;

    @Autowired
    @Lazy
    ObjectMapper objectMapper;

    @Autowired
    protected AuditReader auditReader;

    @Autowired
    @Lazy
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    @Lazy
    protected GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    @Lazy
    protected UserProviderService userProviderService;

    @Autowired
    protected List<BaseEntityPermissionEvaluator<?>> permissionEvaluators;

    @Autowired
    protected DefaultPermissionEvaluator defaultPermissionEvaluator;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<S> findAll() {
        return (List<S>) repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<S> findAll(Pageable pageable) {
        // note: security check is done in permission evaluator
        Optional<User> userOpt = userProviderService.getUserBySession();

        Class<? extends BaseEntity> entityClass = this.getBaseEntityClass();

        // todo: can this be simplified? autowiring BaseEntityPermissionEvaluator did not work.
        BaseEntityPermissionEvaluator entityPermissionEvaluator =
            this.getPermissionEvaluatorForClass(entityClass.getCanonicalName());

        return entityPermissionEvaluator.findAll(userOpt.orElse(null), pageable, repository, entityClass);
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<S> findAllBy(Specification specification) {
        return (List<S>) repository.findAll(specification);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<S> findAllBy(Specification specification, Pageable pageable) {
        return (Page<S>) repository.findAll(specification, pageable);
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<S> findOne(Long id) {
        return repository.findById(id);
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<S> findAllById(List<Long> id) {
        return (List<S>) repository.findAllById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    @Transactional(readOnly = true)
    public Revisions<Integer, S> findRevisions(S entity) {
        return repository.findRevisions(entity.getId());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    @Transactional(readOnly = true)
    public Optional<Revision<Integer, S>> findRevision(S entity, Integer rev) {
        return repository.findRevision(entity.getId(), rev);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'READ')")
    @Transactional(readOnly = true)
    public Optional<Revision<Integer, S>> findLastChangeRevision(S entity) {
        return repository.findLastChangeRevision(entity.getId());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'CREATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S create(S entity) {
        S persistedEntity = repository.save(entity);

        Optional<User> userBySession = userProviderService.getUserBySession();
        if (userBySession.isEmpty()) {
            throw new RuntimeException("Could not detect the logged in user.");
        }
        userInstancePermissionService.setPermission(persistedEntity, userBySession.get(), PermissionCollectionType.ADMIN);

        return persistedEntity;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S update(Long id, S entity) throws IOException {
        Optional<S> persistedEntityOpt = repository.findById(id);

        // Ensure the created timestamp will not be overridden.
        S persistedEntity = persistedEntityOpt.orElseThrow();
        entity.setCreated(persistedEntity.getCreated());

        return repository.save(entity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S updatePartial(S entity, JsonMergePatch patch) throws IOException, JsonPatchException {
        JsonNode entityNode = objectMapper.valueToTree(entity);
        JsonNode patchedEntityNode = patch.apply(entityNode);
        S updatedEntity = objectMapper.readerForUpdating(entity).readValue(patchedEntityNode);
        return repository.save(updatedEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'DELETE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(S entity) {
        userInstancePermissionService.deleteAllFor(entity);

        groupInstancePermissionService.deleteAllFor(entity);

        repository.delete(entity);
    }

    /**
     * Get a historic {@link BaseEntity} for a given time
     *
     * @param id The id of the entity
     * @param time
     * @return A single {@link BaseEntity}
     */
    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<S> findOneByTime(
        Long id,
        OffsetDateTime time
    ) {
        Class<? extends BaseEntity> clazz = getBaseEntityClass();
        if (clazz == null) {
            return Optional.empty();
        }
        List<S> revisions = (List<S>) auditReader.createQuery()
            .forRevisionsOfEntity(clazz, true, true)
            .add(AuditEntity.id().eq(id))
            .add(AuditEntity.revisionProperty("timestamp").le(time.toInstant().toEpochMilli()))
            .addOrder(AuditEntity.revisionProperty("timestamp").desc())
            .setMaxResults(1)
            .getResultList();

        return revisions.stream().findFirst();
    }

    /**
     * Returns the class of the {@link BaseEntity} this abstract class
     * has been declared with, e.g. 'Application.class'.
     *
     * @return The class.
     */
    public Class<? extends BaseEntity> getBaseEntityClass() {
        Class<? extends BaseEntity>[] resolvedTypeArguments = (Class<? extends BaseEntity>[]) GenericTypeResolver.resolveTypeArguments(
            getClass(), BaseService.class
        );

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1];
        } else {
            return null;
        }
    }

    protected BaseEntityPermissionEvaluator getPermissionEvaluatorForClass(String persistentObjectClass) {
        BaseEntityPermissionEvaluator entityPermissionEvaluator = permissionEvaluators.stream()
            .filter(permissionEvaluator -> persistentObjectClass.equals(
                permissionEvaluator.getEntityClassName().getCanonicalName()))
            .findAny()
            .orElse(defaultPermissionEvaluator);

        return entityPermissionEvaluator;
    }
}
