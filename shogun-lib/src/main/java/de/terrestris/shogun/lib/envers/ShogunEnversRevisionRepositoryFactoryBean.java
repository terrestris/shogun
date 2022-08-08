/*
 * Modified work - Copyright 2012-2021 the original author or authors.
 * See https://github.com/spring-projects/spring-data-envers/blob/master/src/main/java/org/springframework/data/envers/repository/support/EnversRevisionRepositoryFactoryBean.java
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
package de.terrestris.shogun.lib.envers;

import de.terrestris.shogun.lib.repository.impl.ShogunRevisionRepositoryImpl;
import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.envers.repository.support.ReflectionRevisionEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

import javax.persistence.EntityManager;
import java.util.Optional;

public class ShogunEnversRevisionRepositoryFactoryBean<T extends RevisionRepository<S, ID, N>, S, ID, N extends Number & Comparable<N>>
    extends EnversRevisionRepositoryFactoryBean<T, S, ID, N> {

    private Class<?> revisionEntityClass;

    /**
     * Creates a new {@link EnversRevisionRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ShogunEnversRevisionRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Configures the revision entity class. Will default to {@link DefaultRevisionEntity}.
     *
     * @param revisionEntityClass
     */
    public void setRevisionEntityClass(Class<?> revisionEntityClass) {
        this.revisionEntityClass = revisionEntityClass;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean#createRepositoryFactory(javax.persistence.EntityManager)
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new ShogunEnversRevisionRepositoryFactoryBean.RevisionRepositoryFactory<T, ID, N>(entityManager, revisionEntityClass);
    }

    /**
     * Repository factory creating {@link RevisionRepository} instances.
     *
     * @author Oliver Gierke
     * @author Jens Schauder
     */
    private static class RevisionRepositoryFactory<T, ID, N extends Number & Comparable<N>> extends JpaRepositoryFactory {

        private final RevisionEntityInformation revisionEntityInformation;
        private final EntityManager entityManager;

        /**
         * Creates a new {@link ShogunEnversRevisionRepositoryFactoryBean.RevisionRepositoryFactory} using the given {@link EntityManager} and revision entity class.
         *
         * @param entityManager must not be {@literal null}.
         * @param revisionEntityClass can be {@literal null}, will default to {@link DefaultRevisionEntity}.
         */
        public RevisionRepositoryFactory(EntityManager entityManager, Class<?> revisionEntityClass) {

            super(entityManager);

            this.entityManager = entityManager;
            this.revisionEntityInformation = Optional.ofNullable(revisionEntityClass)
                .filter(it -> !it.equals(DefaultRevisionEntity.class))
                .<RevisionEntityInformation> map(ReflectionRevisionEntityInformation::new)
                .orElseGet(DefaultRevisionEntityInformation::new);
        }

        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

            Object fragmentImplementation = getTargetRepositoryViaReflection(
                ShogunRevisionRepositoryImpl.class,
                getEntityInformation(metadata.getDomainType()),
                revisionEntityInformation,
                entityManager
            );

            return RepositoryComposition.RepositoryFragments
                .just(fragmentImplementation)
                .append(super.getRepositoryFragments(metadata));
        }
    }

}
