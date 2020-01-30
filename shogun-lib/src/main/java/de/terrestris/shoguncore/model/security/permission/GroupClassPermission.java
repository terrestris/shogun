package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.Group;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "groupclasspermissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupClassPermission extends ClassPermission {

    @ManyToOne(optional = false)
    private Group group;

}