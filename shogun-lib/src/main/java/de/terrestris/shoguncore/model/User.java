package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.terrestris.shoguncore.model.jsonb.UserClientConfig;
import de.terrestris.shoguncore.model.jsonb.UserDetails;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.time.OffsetDateTime;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column
    private boolean enabled;

    @Column
    private OffsetDateTime lastLogin;

    @Column
    private boolean isDemoUser;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private UserDetails details;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private UserClientConfig clientConfig;

}

