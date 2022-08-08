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
package de.terrestris.shogun.interceptor.service;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.interceptor.repository.InterceptorRuleRepository;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class InterceptorRuleService extends BaseService<InterceptorRuleRepository, InterceptorRule> {

    @Transactional(readOnly = true)
    public List<InterceptorRule> findAllRulesForServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event) {
        return repository.findAllByServiceAndEvent(service, event);
    }

    /**
     * Remove all interceptor rules for certain endpoint
     *
     * @param endpoint The endpoint, e.g. the qualified layer name
     */
    public void removeAllRulesForEndpoint(String endpoint) {
        List<InterceptorRule> interceptorRules = repository.findByEndPoint(endpoint);
        if (!interceptorRules.isEmpty()) {
            repository.deleteAll(interceptorRules);
        }
    }

    /**
     * Add request {@link InterceptorRule} for endpoint (e.g. qualified layer name), rule (e.g. MODIFY) and service (e.g. WMS)
     *
     * @param endpoint The qualified layer name
     * @param rule     The {@link InterceptorEnum.RuleType}
     * @param service  The {@link OgcEnum.ServiceType}
     */
    public void addRequestRuleForServiceAndEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        HttpEnum.EventType eventType = HttpEnum.EventType.REQUEST;
        Set<OgcEnum.OperationType> allOperationsForService = OgcEnum.OPERATIONS_BY_SERVICETYPE.get(service);
        allOperationsForService.forEach(operationType -> addRule(eventType, rule, service, operationType, endpoint));
    }

    /**
     * Add response {@link InterceptorRule} for endpoint (e.g. qualified layer name), rule (e.g. MODIFY) and service (e.g. WMS)
     *
     * @param endpoint The qualified layer name
     * @param rule     The {@link InterceptorEnum.RuleType}
     * @param service  The {@link OgcEnum.ServiceType}
     */
    public void addResponseRuleForServiceAndEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        HttpEnum.EventType eventType = HttpEnum.EventType.RESPONSE;
        Set<OgcEnum.OperationType> allOperationsForService = OgcEnum.OPERATIONS_BY_SERVICETYPE.get(service);
        allOperationsForService.forEach(operationType -> addRule(eventType, rule, service, operationType, endpoint));
    }

    /**
     * Add request and response {@link InterceptorRule}s for endpoint (e.g. qualified layer name), rule (e.g. MODIFY) and service (e.g. WMS)
     *
     * @param endpoint The qualified layer name
     * @param rule     The {@link InterceptorEnum.RuleType}
     * @param service  The {@link OgcEnum.ServiceType}
     */
    public void addRuleForEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        addRequestRuleForServiceAndEndpoint(endpoint, rule, service);
        addResponseRuleForServiceAndEndpoint(endpoint, rule, service);
    }

    /**
     * Adds WMS request and response modify rules for endpoint (e.g. qualified layer name)
     *
     * @param endpoint The qualified layer name
     */
    public void setModifyForAllWmsActions(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WMS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRuleForEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     * Adds WFS request and response modify rules for endpoint (e.g. qualified layer name)
     *
     * @param endpoint The qualified layer name
     */
    public void setModifyForAllWfsActions(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WFS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRuleForEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     * Adds Wms request modify rules for endpoint (e.g. qualified layer name)
     *
     * @param endpoint The qualified layer name
     */
    public void setModifyForAllWmsRequests(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WMS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRequestRuleForServiceAndEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     * Adds Wfs request modify rules for endpoint (e.g. qualified layer name)
     *
     * @param endpoint The qualified layer name
     */
    public void setModifyForAllWfsRequests(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WFS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRequestRuleForServiceAndEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     * Add {@link InterceptorRule} to database if not exists, otherwise update
     *
     * @param event     The {@link HttpEnum.EventType}, e.g. request
     * @param rule      The {@link InterceptorEnum.RuleType}, e.g. MODIFY
     * @param service   The {@link OgcEnum.ServiceType}, e.g. WMS
     * @param operation The {@link OgcEnum.OperationType}, e.g. getMap
     * @param endPoint  The endpoint, e.g. the qualified layer name
     */
    public void addRule(HttpEnum.EventType event, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service, OgcEnum.OperationType operation, String endPoint) {
        Optional<InterceptorRule> existingRule = repository.findByEventAndRuleAndServiceAndOperationAndEndPoint(event, rule, service, operation, endPoint);
        InterceptorRule interceptorRule = existingRule.orElseGet(InterceptorRule::new);

        // set / update interceptor rule
        interceptorRule.setEvent(event);
        interceptorRule.setRule(rule);
        interceptorRule.setEndPoint(endPoint);
        interceptorRule.setService(service);
        interceptorRule.setOperation(operation);

        repository.save(interceptorRule);
    }

}
