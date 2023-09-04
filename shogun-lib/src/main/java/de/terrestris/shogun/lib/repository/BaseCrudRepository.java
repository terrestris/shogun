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

import com.jayway.jsonpath.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import javax.persistence.QueryHint;
import java.util.List;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends
    RevisionRepository<T, ID, Integer>, CrudRepository<T, ID>,
    ShogunRevisionRepository<T, ID, Integer>,
    PagingAndSortingRepository<T, ID> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<T> findAll();

    /**
     * Returns a {@link Page} of entities for which the user with userId has permission via UserInstancePermission.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be {@literal null}.
     * @param userId ID of the authenticated user.
     * @return A page of entities.
     */
    @Query(
        value = """
            SELECT
                *
            FROM
                {h-schema}#{#entityName} e
            WHERE (EXISTS (
                SELECT
                    1
                FROM
                    {h-schema}userinstancepermissions uip
                LEFT JOIN {h-schema}permissions p ON p.id = uip.permission_id
                WHERE
                    uip.user_id = :userId AND
                    uip.entity_id = e.id AND
                    p."name" IN (
                        'ADMIN',
                        'READ',
                        'CREATE_READ',
                        'CREATE_READ_UPDATE',
                        'CREATE_READ_DELETE',
                        'READ_UPDATE',
                        'READ_DELETE',
                        'READ_UPDATE_DELETE'
                    )
            )) AND (
                :#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} = '$' OR CAST(row_to_json(e) AS JSONB) @@ CAST(:#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} AS JSONPATH)
            )
        """,
        // TODO This should include the exists filter
        countQuery = """
            SELECT
                COUNT(e.*)
            FROM
                {h-schema}#{#entityName} e
        """,
        nativeQuery = true
    )
    @QueryHints(
        value = @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
        forCounting = false
    )
    Page<T> findAll(Pageable pageable, Filter filter, Long userId);

    /**
     * Returns a {@link Page} of entities for which the user with userId has permission via UserInstancePermission or GroupInstancePermission.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be {@literal null}.
     * @param userId ID of the authenticated user.
     * @param groupIds All IDs of the groups of the authenticated user.
     * @return A page of entities.
     */
    @Query(
        value = """
            SELECT
                *
            FROM
                {h-schema}#{#entityName} e
            WHERE (EXISTS (
                SELECT
                    1
                FROM
                    {h-schema}userinstancepermissions uip
                LEFT JOIN {h-schema}permissions p ON p.id = uip.permission_id
                WHERE
                    uip.user_id = :userId AND
                    uip.entity_id = e.id AND
                    p."name" IN (
                        'ADMIN',
                        'READ',
                        'CREATE_READ',
                        'CREATE_READ_UPDATE',
                        'CREATE_READ_DELETE',
                        'READ_UPDATE',
                        'READ_DELETE',
                        'READ_UPDATE_DELETE'
                    )
                ) OR EXISTS (
                    SELECT
                        1
                    FROM
                        {h-schema}groupinstancepermissions gip
                    LEFT JOIN {h-schema}permissions p ON p.id = gip.permission_id
                    WHERE
                        gip.group_id IN :groupIds AND
                        gip.entity_id = e.id AND
                        p."name" IN (
                            'ADMIN',
                            'READ',
                            'CREATE_READ',
                            'CREATE_READ_UPDATE',
                            'CREATE_READ_DELETE',
                            'READ_UPDATE',
                            'READ_DELETE',
                            'READ_UPDATE_DELETE'
                        )
                )) AND (
                    :#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} = '$' OR CAST(row_to_json(e) AS JSONB) @@ CAST(:#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} AS JSONPATH)
                )
        """,
        countQuery = """
            SELECT
                COUNT(e.*)
            FROM
                {h-schema}#{#entityName} e
        """,
        nativeQuery = true
    )
    @QueryHints(
        value = @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
        forCounting = false
    )
    Page<T> findAll(Pageable pageable, Filter filter, Long userId, List<Long> groupIds);

    /**
     * Returns a {@link Page} of entities without checking any permissions.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     * @return A page of entities.
     */
    @Query(
        value = """
            SELECT
                e.*
            FROM
                {h-schema}#{#entityName} e
            WHERE
                :#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} = '$' OR CAST(row_to_json(e) AS JSONB) @@ CAST(:#{T(de.terrestris.shogun.lib.util.JsonPathFilterUtil).writeFilter(#filter)} AS JSONPATH)
        """,
        countQuery = """
            SELECT
                COUNT(e.*)
            FROM
                {h-schema}#{#entityName} e
        """,
        nativeQuery = true
    )
    @QueryHints(
        value = @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
        forCounting = false
    )
    Page<T> findAll(Pageable pageable, Filter filter);

}
