package de.terrestris.shogun.interceptor.repository;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterceptorRuleRepository extends BaseCrudRepository<InterceptorRule, Long>, JpaSpecificationExecutor<InterceptorRule> {

    List<InterceptorRule> findAllByServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event);

}
