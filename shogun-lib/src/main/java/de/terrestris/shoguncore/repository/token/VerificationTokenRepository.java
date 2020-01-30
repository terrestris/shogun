package de.terrestris.shoguncore.repository.token;

import de.terrestris.shoguncore.model.token.VerificationToken;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends BaseCrudRepository<VerificationToken, Long>, JpaSpecificationExecutor<VerificationToken> {
}
