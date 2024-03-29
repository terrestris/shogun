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
package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// https://insource.io/blog/articles/custom-authorization-with-spring-boot.html
public interface EntityPermissionEvaluator<E> {
    Class<E> getEntityClassName();

    boolean hasPermission(User user, E entity, PermissionType permission);

    boolean hasPermission(User user, Long entityId, String targetDomainType, PermissionType permission);

    boolean hasPermission(User user, Class<?> clazz, PermissionType permission);

    /**
     * Custom permission evaluators have to implement this method to provide a way to check permissions for requests
     * with pagination. See {@link BaseEntityPermissionEvaluator#findAll(User, Pageable, BaseCrudRepository)} for the
     * default implementation for {@link de.terrestris.shogun.lib.model.BaseEntity}.
     */
    Page<E> findAll(User user, Pageable pageable, BaseCrudRepository<E, Long> repository, Class<E> baseEntityClass);
}
