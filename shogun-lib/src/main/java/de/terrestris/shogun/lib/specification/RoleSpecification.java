package de.terrestris.shogun.lib.specification;

import de.terrestris.shogun.lib.model.Role;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {

    public static Specification<Role> findByName(String name) {
        return (root, query, cb) -> {
            return cb.equal(root.get("name"), name);
        };
    }
}
