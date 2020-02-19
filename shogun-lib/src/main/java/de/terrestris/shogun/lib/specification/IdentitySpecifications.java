package de.terrestris.shogun.lib.specification;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.security.Identity;
import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.User;
import org.springframework.data.jpa.domain.Specification;

public class IdentitySpecifications {

    public static Specification<Identity> hasUser(User user) {
        return (root, query, cb) -> {
            return cb.equal(root.get("user"), user);
        };
    }

    public static Specification<Identity> hasGroup(Group group) {
        return (root, query, cb) -> {
            return cb.equal(root.get("group"), group);
        };
    }

    public static Specification<Identity> hasRole(Role role) {
        return (root, query, cb) -> {
            return cb.equal(root.get("role"), role);
        };
    }
}
