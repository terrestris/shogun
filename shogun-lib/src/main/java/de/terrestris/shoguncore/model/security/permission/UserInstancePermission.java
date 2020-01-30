package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "userinstancepermissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserInstancePermission extends InstancePermission {

    @ManyToOne(optional = false)
    private User user;

}