package de.terrestris.shoguncore.specification.token;

import de.terrestris.shoguncore.model.token.UserVerificationToken;
import org.springframework.data.jpa.domain.Specification;

public class UserVerificationTokenSpecification {

    public static Specification<UserVerificationToken> findByToken(String token) {
        return (root, query, cb) -> {
            return cb.equal(root.get("token"), token);
        };
    }
}
