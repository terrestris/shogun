package de.terrestris.shogun.lib.specification.token;

import de.terrestris.shogun.lib.model.token.VerificationToken;
import org.springframework.data.jpa.domain.Specification;

public class VerificationTokenSpecification {

    public static Specification<VerificationToken> findByToken(String token) {
        return (root, query, cb) -> {
            return cb.equal(root.get("token"), token);
        };
    }
}
