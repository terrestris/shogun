package de.terrestris.shogun.interceptor.repository;

import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterceptorRuleRepository extends BaseCrudRepository<InterceptorRule, Long>, JpaSpecificationExecutor<InterceptorRule> {
}
