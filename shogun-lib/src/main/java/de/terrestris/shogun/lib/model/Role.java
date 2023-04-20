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
package de.terrestris.shogun.lib.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity(name = "roles")
@Table(schema = "shogun")
@DynamicUpdate
@Audited
@AuditTable(value = "roles_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Role<T> extends BaseEntity {

    @Column(unique = true, nullable = false)
    @Schema(
        description = "The backend ID of the user.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String authProviderId;

    @Transient
    @Schema(
        description = "The role details stored in the associated provider.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private T providerDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role<?> user = (Role<?>) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

