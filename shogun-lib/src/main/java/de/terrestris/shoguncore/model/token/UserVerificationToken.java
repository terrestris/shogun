package de.terrestris.shoguncore.model.token;

import de.terrestris.shoguncore.model.User;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
