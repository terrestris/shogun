/*
 * Copyright 2012-2021 the original author or authors.
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

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

/**
 * {@link RevisionEntityInformation} for {@link DefaultRevisionEntity}.
 *
 * @author Oliver Gierke
 */
class DefaultRevisionEntityInformation implements RevisionEntityInformation {

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionNumberType()
     */
    public Class<?> getRevisionNumberType() {
        return Integer.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#isDefaultRevisionEntity()
     */
    public boolean isDefaultRevisionEntity() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionEntityClass()
     */
    public Class<?> getRevisionEntityClass() {
        return DefaultRevisionEntity.class;
    }
}
