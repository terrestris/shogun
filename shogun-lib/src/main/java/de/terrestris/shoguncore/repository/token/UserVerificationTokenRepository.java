package de.terrestris.shoguncore.repository.token;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.UserVerificationToken;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationTokenRepository extends BaseCrudRepository<UserVerificationToken, Long>, JpaSpecificationExecutor<UserVerificationToken> {

    Optional<UserVerificationToken> findByUser(User user);

    List<UserVerificationToken> findAllByUser(User user);

    List<UserVerificationToken> findByExpiryDateBefore(Date date);

}
