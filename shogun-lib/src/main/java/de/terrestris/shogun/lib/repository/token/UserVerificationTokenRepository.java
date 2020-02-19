package de.terrestris.shogun.lib.repository.token;

import de.terrestris.shogun.lib.model.token.UserVerificationToken;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationTokenRepository extends BaseCrudRepository<UserVerificationToken, Long>, JpaSpecificationExecutor<UserVerificationToken> {
}
