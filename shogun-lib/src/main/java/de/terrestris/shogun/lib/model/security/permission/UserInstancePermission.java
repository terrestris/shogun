package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "userinstancepermissions")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserInstancePermission extends InstancePermission {

    @ManyToOne(optional = false)
    private User user;

}
