package de.terrestris.shoguncore.model.security;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "identities")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Identity extends BaseEntity {

    @OneToOne
    private User user;

    @OneToOne
    private Group group;

    @OneToOne
    private Role role;

}
