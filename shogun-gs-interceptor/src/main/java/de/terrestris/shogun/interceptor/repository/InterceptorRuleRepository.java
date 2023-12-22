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
package de.terrestris.shogun.interceptor.repository;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterceptorRuleRepository extends BaseCrudRepository<InterceptorRule, Long>, JpaSpecificationExecutor<InterceptorRule> {

    @QueryHints(value = {
        @QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"),
        @QueryHint(name = AvailableHints.HINT_CACHE_REGION, value = "interceptorrule_query")
    })
    List<InterceptorRule> findAllByServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event);

    @QueryHints(value = {
        @QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"),
        @QueryHint(name = AvailableHints.HINT_CACHE_REGION, value = "interceptorrule_query")
    })
    List<InterceptorRule> findByEndPoint(String endpoint);

    @QueryHints(value = {
        @QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"),
        @QueryHint(name = AvailableHints.HINT_CACHE_REGION, value = "interceptorrule_query")
    })
    Optional<InterceptorRule> findByEventAndRuleAndServiceAndOperationAndEndPoint(HttpEnum.EventType event, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service, OgcEnum.OperationType operation, String endPoint);

}
