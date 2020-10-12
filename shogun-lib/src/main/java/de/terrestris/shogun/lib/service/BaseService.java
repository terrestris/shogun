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
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.BasePermissionEvaluator;
import de.terrestris.shogun.lib.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseService<T extends BaseCrudRepository<S, Long> & JpaSpecificationExecutor<S>, S extends BaseEntity> {

    @Autowired
    protected T repository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    protected AuditReader auditReader;

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    protected BasePermissionEvaluator basePermissionEvaluator;

    @Autowired
    protected GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<S> findAll() {
        return (List<S>) repository.findAll();
    }

    // @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<S> findAllWithWorkaround(Pageable pageable) {
        Page<S> pageResult = (Page<S>) repository.findAll(pageable);
        if (securityContextUtil.hasRole("ROLE_ADMIN")) {
           return pageResult;
        }
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        List<S> foundResults = new ArrayList<>();
        List<S> filteredResults = fetchUntilPageSizeIsReached(pageResult.getContent(), foundResults, pageResult.getTotalElements(), pageable, authentication);
        return new PageImpl<>(filteredResults, pageable, filteredResults.size());
    }

    // security check is done on repository
    @Transactional(readOnly = true)
    public Page<S> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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

        userInstancePermissionService.setPermission(persistedEntity, PermissionCollectionType.ADMIN);

        return persistedEntity;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S update(Long id, S entity) throws IOException {
        Optional<S> persistedEntityOpt = repository.findById(id);

        ObjectNode jsonObject = objectMapper.valueToTree(entity);

        // Ensure the created timestamp will not be overridden.
        S persistedEntity = persistedEntityOpt.orElseThrow();
        OffsetDateTime createdTimestamp = persistedEntity.getCreated();
        String serialized = createdTimestamp != null ? createdTimestamp.toInstant().toString() : null;
        jsonObject.put("created", serialized);

        S updatedEntity = objectMapper.readerForUpdating(persistedEntity).readValue(jsonObject);

        return repository.save(updatedEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public S updatePartial(Long entityId, S entity, Map<String, Object> values) throws IOException {
        if (ObjectUtils.notEqual(entityId, entity.getId())) {
            throw new IOException("ID's of passed entity and parameter do not match. No partial update possible");
        }
        JsonNode jsonObject = objectMapper.valueToTree(values);
        S updatedEntity = objectMapper.readerForUpdating(entity).readValue(jsonObject);
        return repository.save(updatedEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#entity, 'DELETE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(S entity) {
        userInstancePermissionService.deleteAllForEntity(entity);

        groupInstancePermissionService.deleteAllForEntity(entity);

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
        Class<? extends BaseEntity>[] resolvedTypeArguments = (Class<? extends BaseEntity>[]) GenericTypeResolver.resolveTypeArguments(getClass(),
            BaseService.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1];
        } else {
            return null;
        }
    }

    // currently not needed
    public List<S> fetchUntilPageSizeIsReached(List<S> pageResult, List<S> filteredResults, long totalResults, Pageable pageable, Authentication auth) {
        if (pageable.getOffset() >= totalResults) {
            LOG.info("Last page {} reached. Returning {} filtered results, of {} total", pageable.getPageNumber(), pageResult.size(), totalResults);
            return filteredResults;
        }

        LOG.info("Filtering {} new elements", pageResult.size());
        List<S> filteredPageResult = pageResult.stream()
            .filter(pr -> basePermissionEvaluator.hasPermission(auth, pr, "READ"))
            .collect(Collectors.toList());

        if (filteredPageResult.size() == pageResult.size()) {
            // page filled, return results
            return pageResult;
        } else {
            // fetch more results to fill page
            Pageable nextPage = pageable.next();
            List<S> nextPageResult = repository.findAll(nextPage).getContent();
            filteredResults.addAll(filteredPageResult);
            LOG.info("Not enough results to fill page, checking for more results in next page {}", nextPage.getPageNumber());
            return fetchUntilPageSizeIsReached(nextPageResult, filteredResults, totalResults, nextPage, auth);
        }
    }
}
