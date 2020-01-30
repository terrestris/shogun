package de.terrestris.shoguncore.model.security;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
