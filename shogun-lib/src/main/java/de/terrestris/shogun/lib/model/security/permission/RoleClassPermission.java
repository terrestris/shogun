/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.Role;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity(name = "roleclasspermissions")
@Table(schema = "shogun")
@DynamicUpdate
@Audited
@AuditTable(value = "roleclasspermissions_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "roleclasspermissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RoleClassPermission extends ClassPermission {

    @ManyToOne(optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleClassPermission that = (RoleClassPermission) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
