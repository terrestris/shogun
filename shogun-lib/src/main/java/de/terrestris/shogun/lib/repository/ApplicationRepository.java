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

import de.terrestris.shogun.lib.model.Application;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends BaseCrudRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<Application> findByName(String name);

    // https://github.com/spring-projects/spring-data-jpa/issues/2644
//    @Query(
//        value = "SELECT * FROM {h-schema}applications a WHERE jsonb_path_exists(a.layer_tree, '$.** \\\\?\\\\? (@.\"layerId\" == $id)', jsonb_build_object('id', :layerId))",
//        nativeQuery = true
//    )
//    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
//    List<Application> findAllByLayerId(@Param("layerId") Long layerId);

//    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
//    @Query(
//        value = "SELECT * FROM shogun.applications a WHERE jsonb_path_exists(a.layer_tree, '$.** ? (@.\"layerId\" == $id)', jsonb_build_object('id', :layerId))",
//        nativeQuery = true
//    )
//    List<Application> findAllApplicationsContainingLayer(Long layerId);
//
//    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
//    @Query(
//        value = "SELECT a FROM Application a WHERE a.public_access = true"
//    )
//    Page<Application> findAllOpenApplications(Pageable pageable);
}
