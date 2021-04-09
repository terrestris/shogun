/*
 * Modified work - Copyright 2012-2021 the original author or authors.
 * See https://github.com/spring-projects/spring-data-envers/blob/master/src/main/java/org/springframework/data/envers/repository/support/EnversRevisionRepositoryImpl.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.repository.impl;

import de.terrestris.shogun.lib.envers.CustomAnnotationRevisionMetadata;
import de.terrestris.shogun.lib.envers.CustomRevisionMetadata;
import de.terrestris.shogun.lib.repository.CustomRevisionRepository;
import org.hibernate.envers.*;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryImpl;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.history.support.RevisionEntityInformation;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.history.RevisionMetadata.RevisionType.*;

public class CustomRevisionRepositoryImpl<T, ID, N extends Number & Comparable<N>> extends EnversRevisionRepositoryImpl<T, ID, N> implements CustomRevisionRepository<T, ID, N> {

    private final EntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;

    public CustomRevisionRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                        RevisionEntityInformation revisionEntityInformation, EntityManager entityManager) {
        super(entityInformation, revisionEntityInformation, entityManager);
        Assert.notNull(revisionEntityInformation, "RevisionEntityInformation must not be null!");
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Revisions<N, T> findRevisions(ID id) {
        Class<T> type = entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Object[]> resultList = reader.createQuery()
            .forRevisionsOfEntityWithChanges(type, true)
            .add(AuditEntity.id().eq(id))
            .getResultList();

        List<Revision<N, T>> revisionList = new ArrayList<>(resultList.size());

        for (Object[] objects : resultList) {
            revisionList.add(createRevision(new QueryResult<>(objects)));
        }

        return Revisions.of(revisionList);
    }

    @SuppressWarnings("unchecked")
    private Revision<N, T> createRevision(QueryResult<T> queryResult) {
        return Revision.of((RevisionMetadata<N>) queryResult.createRevisionMetadata(), queryResult.entity);
    }

    @SuppressWarnings("unchecked")
    static class QueryResult<T> {

        private final T entity;
        private final Object metadata;
        private final RevisionMetadata.RevisionType revisionType;
        private final Object changedFields;

        QueryResult(Object[] data) {

            Assert.notNull(data, "Data must not be null");
            Assert.isTrue( //
                data.length == 4, //
                () -> String.format("Data must have length three, but has length %d.", data.length));
            Assert.isTrue( //
                data[2] instanceof RevisionType, //
                () -> String.format("The third array element must be of type Revision type, but is of type %s",
                    data[2].getClass()));

            entity = (T) data[0];
            metadata = data[1];
            revisionType = convertRevisionType((RevisionType) data[2]);
            changedFields = data[3];
//            String[] changedFieldsArray = (String[]) data[3];
//            if (changedFieldsArray) {
//                changedFields = Arrays.asList(data[3])
//            }
        }

        RevisionMetadata<?> createRevisionMetadata() {

            return metadata instanceof DefaultRevisionEntity //
                ? new CustomRevisionMetadata((DefaultRevisionEntity) metadata, revisionType, (Set<String>) changedFields) //
                : new CustomAnnotationRevisionMetadata<>(metadata, RevisionNumber.class, RevisionTimestamp.class, revisionType, (Set<String>) changedFields);
        }

        private static RevisionMetadata.RevisionType convertRevisionType(RevisionType datum) {
            switch (datum) {
                case ADD:
                    return INSERT;
                case MOD:
                    return UPDATE;
                case DEL:
                    return DELETE;
                default:
                    return UNKNOWN;
            }
        }
    }

}
