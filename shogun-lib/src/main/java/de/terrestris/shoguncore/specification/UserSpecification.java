package de.terrestris.shoguncore.specification;

import de.terrestris.shoguncore.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> findByUserName(String userName) {
        return (root, query, cb) -> {
            return cb.equal(root.get("username"), userName);
        };
    }

    public static Specification<User> findByMail(String email) {
        return (root, query, cb) -> {
            return cb.equal(root.get("email"), email);
        };
    }
}
