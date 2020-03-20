package de.terrestris.shogun.interceptor.service;

import de.terrestris.shogun.lib.service.BaseService;
import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.terrestris.shogun.interceptor.repository.InterceptorRuleRepository;

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
     * @param endpoint The endpoint, e.g. the qualified layer name
     */
    public void removeAllRulesForEndpoint(String endpoint) {
        List<InterceptorRule> interceptorRules = repository.findByEndPoint(endpoint);
        if (!interceptorRules.isEmpty()) {
            repository.deleteAll(interceptorRules);
        }
    }

    /**
     *
     * @param endpoint
     * @param rule
     * @param service
     */
    public void addRequestRuleForServiceAndEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        HttpEnum.EventType eventType = HttpEnum.EventType.REQUEST;
        Set<OgcEnum.OperationType> allOperationsForService = OgcEnum.OPERATIONS_BY_SERVICETYPE.get(service);
        allOperationsForService.forEach(operationType -> addRule(eventType, rule, service, operationType, endpoint));
    }

    /**
     *
     * @param endpoint
     * @param rule
     * @param service
     */
    public void addResponseRuleForEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        HttpEnum.EventType eventType = HttpEnum.EventType.RESPONSE;
        Set<OgcEnum.OperationType> allOperationsForService = OgcEnum.OPERATIONS_BY_SERVICETYPE.get(service);
        allOperationsForService.forEach(operationType -> addRule(eventType, rule, service, operationType, endpoint));
    }

    /**
     * adds all …
     * @param endpoint
     */
    public void addRuleForEndpoint(String endpoint, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service) {
        addRequestRuleForServiceAndEndpoint(endpoint, rule, service);
        addResponseRuleForEndpoint(endpoint, rule, service);
    }

    /**
     *
     * @param endpoint
     */
    public void setModifyForAllWmsActions(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WMS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRuleForEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     *
     * @param endpoint
     */
    public void setModifyForAllWfsActions(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WFS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRuleForEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     *
     * @param endpoint
     */
    public void setModifyForAllWmsRequests(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WMS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRequestRuleForServiceAndEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     *
     * @param endpoint
     */
    public void setModifyForAllWfsRequests(String endpoint) {
        OgcEnum.ServiceType serviceType = OgcEnum.ServiceType.WFS;
        InterceptorEnum.RuleType modifyRule = InterceptorEnum.RuleType.MODIFY;
        addRequestRuleForServiceAndEndpoint(endpoint, modifyRule, serviceType);
    }

    /**
     *
     * @param event
     * @param rule
     * @param service
     * @param operation
     * @param endPoint
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
