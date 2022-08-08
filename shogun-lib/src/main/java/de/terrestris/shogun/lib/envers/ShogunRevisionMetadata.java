/*
 * Modified work - Copyright 2012-2020 the original author or authors.
 * See https://github.com/spring-projects/spring-data-envers/
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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ShogunRevisionMetadata implements RevisionMetadata<Integer> {

    private final DefaultRevisionEntity entity;
    private final RevisionMetadata.RevisionType revisionType;
    private final Set<String> changedFields;

    public ShogunRevisionMetadata(DefaultRevisionEntity entity) {
        this(entity, RevisionMetadata.RevisionType.UNKNOWN, new HashSet<>());
    }

    public ShogunRevisionMetadata(DefaultRevisionEntity entity, RevisionMetadata.RevisionType revisionType, Set<String> changedFields) {
        Assert.notNull(entity, "DefaultRevisionEntity must not be null");

        this.entity = entity;
        this.revisionType = revisionType;
        this.changedFields = changedFields;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.history.RevisionMetadata#getRevisionNumber()
     */
    public Optional<Integer> getRevisionNumber() {
        return Optional.of(entity.getId());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.history.RevisionMetadata#getRevisionDate()
     */
    @Deprecated
    public Optional<LocalDateTime> getRevisionDate() {
        return getRevisionInstant().map(instant -> LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault()));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.history.RevisionMetadata#getRevisionInstant()
     */
    @Override
    public Optional<Instant> getRevisionInstant() {
        return Optional.of(Instant.ofEpochMilli(entity.getTimestamp()));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.history.RevisionMetadata#getDelegate()
     */
    @SuppressWarnings("unchecked")
    public <T> T getDelegate() {
        return (T) entity;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.history.RevisionMetadata#getRevisionType()
     */
    @Override
    public RevisionMetadata.RevisionType getRevisionType() {
        return revisionType;
    }

    public Set<String> getChangedFields() {
        return changedFields;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShogunRevisionMetadata that = (ShogunRevisionMetadata) o;
        return getRevisionNumber().equals(that.getRevisionNumber())
            && getRevisionInstant().equals(that.getRevisionInstant()) && revisionType.equals(that.getRevisionType());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomRevisionMetadata{" + "entity=" + entity + ", revisionType=" + revisionType + '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(entity).append(revisionType).append(changedFields).toHashCode();
    }
}
