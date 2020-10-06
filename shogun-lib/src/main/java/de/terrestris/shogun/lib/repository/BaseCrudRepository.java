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
package de.terrestris.shogun.lib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import javax.persistence.QueryHint;
import java.util.List;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends RevisionRepository<T, ID, Integer>, CrudRepository<T, ID>, ShogunRevisionRepository<T, ID, Integer> PagingAndSortingRepository<T, ID> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<T> findAll();

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Page<T> findAll(Pageable pageable);

    // @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    //@Query("select m from #{#entityName} m where 1= ?#{ hasRole('ROLE_ADMIN') ? 1 : 0 }")
//    @Query("select m from #{#entityName} m where 1 = ?#{ (hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')) ? 1 : 0 }")
//    @Query("select m from #{#entityName} m where m.id = 1 =  ?#{ (hasRole('ROLE_ADMIN') or hasPermission(m, ,'READ')) ? 1 : 0 }")
    // Page<T> findAll(Pageable pageable);
}
