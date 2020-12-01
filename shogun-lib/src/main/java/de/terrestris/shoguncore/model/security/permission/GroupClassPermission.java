package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.Group;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
