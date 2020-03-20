package de.terrestris.shogun.interceptor.service;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.interceptor.repository.InterceptorRuleRepository;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service()
public class InterceptorRuleService extends BaseService<InterceptorRuleRepository, InterceptorRule> {

    @Transactional(readOnly = true)
    public List<InterceptorRule> findAllRulesForServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event) {
        return repository.findAllByServiceAndEvent(service, event);
    }

    public void addRequestRule() {

    }

    public void addResponseRule() {

    }

    public void addRule() {

    }

    public void removeRequestRule() {

    }

    public void removeResponseRule() {

    }

    public void removeRule() {

    }

}
