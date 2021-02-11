package de.terrestris.shoguncore.model.token;

import de.terrestris.shoguncore.model.BaseEntity;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class VerificationToken extends BaseEntity {

    private static final int DEFAULT_EXPIRATION = 60 * 24;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private OffsetDateTime expiryDate;

    public VerificationToken(final String token) {
        super();

        this.token = token;
        this.expiryDate = calculateExpiryDate(DEFAULT_EXPIRATION);
    }

    public VerificationToken(final String token, int expiration) {
        super();

        this.token = token;
        this.expiryDate = calculateExpiryDate(expiration);
    }

    private OffsetDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return OffsetDateTime.now().plus(expiryTimeInMinutes, ChronoUnit.MINUTES);
    }
}
