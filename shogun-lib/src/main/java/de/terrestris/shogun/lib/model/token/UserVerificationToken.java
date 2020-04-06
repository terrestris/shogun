package de.terrestris.shogun.lib.model.token;

import de.terrestris.shogun.lib.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "userverificationtokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserVerificationToken extends VerificationToken {

    @OneToOne
    private User user;

    public UserVerificationToken(final String token, final User user) {
        super(token);

        this.user = user;
    }

    public UserVerificationToken(final String token, final User user, int expiration) {
        super(token, expiration);

        this.user = user;
    }

}
