package de.terrestris.shoguncore.specification;

import de.terrestris.shoguncore.model.Role;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {

    public static Specification<Role> findByName(String name) {
        return (root, query, cb) -> {
            return cb.equal(root.get("name"), name);
        };
    }
}
