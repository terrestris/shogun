package de.terrestris.shogun.interceptor.controller;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.interceptor.service.InterceptorRuleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interceptorrules")
@Log4j2
public class InterceptorRuleController {

    @Autowired
    protected InterceptorRuleService interceptorRuleService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<InterceptorRule> getAllInterceptorRules() {
        return this.interceptorRuleService.findAll();
    }

    @GetMapping(value = "/service/{service}/event/{event}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<InterceptorRule> findAllRulesForServiceAndEvent(
        @PathVariable(name = "service") String serviceStr,
        @PathVariable(name = "event") String eventStr
    ) {
        HttpEnum.EventType event = HttpEnum.EventType.valueOf(eventStr.toUpperCase());
        OgcEnum.ServiceType service = OgcEnum.ServiceType.valueOf(serviceStr.toUpperCase());
        return this.interceptorRuleService.findAllRulesForServiceAndEvent(service, event);
    }

    @DeleteMapping(value = "/endpoint/{endpoint}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllRulesForEndpoint(@PathVariable String endpoint) {
        this.interceptorRuleService.removeAllRulesForEndpoint(endpoint);
    }

    @PostMapping(value = "/endpoint/{endpoint}/request/{service}/{rule}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRequestRuleForServiceAndEndpoint(@PathVariable String endpoint, @PathVariable(name = "service") String serviceStr, @PathVariable(name = "rule") String ruleStr) {
        InterceptorEnum.RuleType rule = InterceptorEnum.RuleType.valueOf(ruleStr.toUpperCase());
        OgcEnum.ServiceType service = OgcEnum.ServiceType.valueOf(serviceStr.toUpperCase());
        this.interceptorRuleService.addRequestRuleForServiceAndEndpoint(endpoint, rule, service);
    }

    @PostMapping(value = "/endpoint/{endpoint}/response/{service}/{rule}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addResponseRuleForServiceAndEndpoint(@PathVariable String endpoint, @PathVariable(name = "service") String serviceStr, @PathVariable(name = "rule") String ruleStr) {
        InterceptorEnum.RuleType rule = InterceptorEnum.RuleType.valueOf(ruleStr.toUpperCase());
        OgcEnum.ServiceType service = OgcEnum.ServiceType.valueOf(serviceStr.toUpperCase());
        this.interceptorRuleService.addResponseRuleForServiceAndEndpoint(endpoint, rule, service);
    }

    @PostMapping(value = "/endpoint/{endpoint}/all/{service}/{rule}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRuleForEndpoint(@PathVariable String endpoint, @PathVariable(name = "service") String serviceStr, @PathVariable(name = "rule") String ruleStr) {
        InterceptorEnum.RuleType rule = InterceptorEnum.RuleType.valueOf(ruleStr.toUpperCase());
        OgcEnum.ServiceType service = OgcEnum.ServiceType.valueOf(serviceStr.toUpperCase());
        this.interceptorRuleService.addRuleForEndpoint(endpoint, rule, service);
    }

    @PostMapping(value = "/endpoint/{endpoint}/modifyAllWms")
    @ResponseStatus(HttpStatus.OK)
    public void setModifyForAllWmsActions(@PathVariable String endpoint) {
        this.interceptorRuleService.setModifyForAllWmsActions(endpoint);
    }

    @PostMapping(value = "/endpoint/{endpoint}/modifyAllWfs")
    @ResponseStatus(HttpStatus.OK)
    public void setModifyForAllWfsActions(@PathVariable String endpoint) {
        this.interceptorRuleService.setModifyForAllWfsActions(endpoint);
    }

    @PostMapping(value = "/endpoint/{endpoint}/modifyAllWmsRequests")
    @ResponseStatus(HttpStatus.OK)
    public void setModifyForAllWmsRequests(@PathVariable String endpoint) {
        this.interceptorRuleService.setModifyForAllWmsRequests(endpoint);
    }

    @PostMapping(value = "/endpoint/{endpoint}/modifyAllWfsRequests")
    @ResponseStatus(HttpStatus.OK)
    public void setModifyForAllWfsRequests(@PathVariable String endpoint) {
        this.interceptorRuleService.setModifyForAllWfsRequests(endpoint);
    }

}
