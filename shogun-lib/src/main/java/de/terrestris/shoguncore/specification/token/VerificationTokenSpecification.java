package de.terrestris.shoguncore.specification.token;

import de.terrestris.shoguncore.model.token.VerificationToken;
import org.springframework.data.jpa.domain.Specification;

public class VerificationTokenSpecification {

    public static Specification<VerificationToken> findByToken(String token) {
        return (root, query, cb) -> {
            return cb.equal(root.get("token"), token);
        };
    }
}
