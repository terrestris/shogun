package de.terrestris.shogun.interceptor.repository;

import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterceptorRuleRepository extends BaseCrudRepository<InterceptorRule, Long>, JpaSpecificationExecutor<InterceptorRule> {

    List<InterceptorRule> findAllByServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event);

    List<InterceptorRule> findByEndPoint(String endpoint);

    Optional<InterceptorRule> findByEventAndRuleAndServiceAndOperationAndEndPoint(HttpEnum.EventType event, InterceptorEnum.RuleType rule, OgcEnum.ServiceType service, OgcEnum.OperationType operation, String endPoint);
}
