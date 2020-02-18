package de.terrestris.shogun.interceptor.specification;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import org.springframework.data.jpa.domain.Specification;

public class InterceptorRuleSpecification {

    public static Specification<InterceptorRule> findAllByServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event) {
        return (root, query, cb) -> {
            query
                .distinct(true)
                .orderBy(
                    cb.desc(root.get("endPoint")),
                    cb.desc(root.get("operation"))
                );

            return cb.and(
                cb.equal(root.get("service"), service),
                cb.equal(root.get("event"), event)
            );
        };
    }

    public static Specification<InterceptorRule> findAllByService(String service) {
        return (root, query, cb) -> cb.equal(root.get("service"), service);
    }

    public static Specification<InterceptorRule> findAllByEvent(String event) {
        return (root, query, cb) -> cb.equal(root.get("event"), event);
    }
}
