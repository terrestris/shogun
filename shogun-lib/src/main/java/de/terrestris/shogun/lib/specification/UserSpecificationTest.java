package de.terrestris.shogun.lib.specification;

import de.terrestris.shogun.lib.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecificationTest implements Specification<User> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        String operation = criteria.getOperation();
        String key = criteria.getKey();
        Object value = criteria.getValue();

        if (operation.equalsIgnoreCase(">")) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.<String> get(key), value.toString());
        }
        if (operation.equalsIgnoreCase(">")) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.<String> get(key), value.toString());
        } else if (operation.equalsIgnoreCase("<")) {
            return criteriaBuilder.lessThanOrEqualTo(
                    root.<String> get(key), value.toString());
        } else if (operation.equalsIgnoreCase(":")) {
            if (root.get(key).getJavaType() == String.class) {
                return criteriaBuilder.like(
                        root.<String>get(key), "%" + value + "%");
            } else {
                return criteriaBuilder.equal(root.get(key), value);
            }
        }
        return null;
    }
}
