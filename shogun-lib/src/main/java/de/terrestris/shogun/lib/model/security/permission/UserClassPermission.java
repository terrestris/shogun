package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "userclasspermissions")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserClassPermission extends ClassPermission {

    @ManyToOne(optional = false)
    private User user;

}
