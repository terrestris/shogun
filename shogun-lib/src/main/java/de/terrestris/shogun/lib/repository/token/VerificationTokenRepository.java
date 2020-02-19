package de.terrestris.shogun.lib.repository.token;

import de.terrestris.shogun.lib.model.token.VerificationToken;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends BaseCrudRepository<VerificationToken, Long>, JpaSpecificationExecutor<VerificationToken> {
}
