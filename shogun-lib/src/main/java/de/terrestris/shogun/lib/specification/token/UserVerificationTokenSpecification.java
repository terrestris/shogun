package de.terrestris.shogun.lib.specification.token;

import de.terrestris.shogun.lib.model.token.UserVerificationToken;
import org.springframework.data.jpa.domain.Specification;

public class UserVerificationTokenSpecification {

    public static Specification<UserVerificationToken> findByToken(String token) {
        return (root, query, cb) -> {
            return cb.equal(root.get("token"), token);
        };
    }
}
