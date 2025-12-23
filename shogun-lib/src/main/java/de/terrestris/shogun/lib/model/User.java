/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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

import de.terrestris.shogun.lib.model.jsonb.UserClientConfig;
import de.terrestris.shogun.lib.model.jsonb.UserDetails;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity(name = "users")
@Table(schema = "shogun")
@DynamicUpdate
@Audited
@AuditTable(value = "users_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class User<T> extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String authProviderId;

    @Transient
    private T providerDetails;

    @Column(columnDefinition = "jsonb", name = "details")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserDetails details;

    @Column(columnDefinition = "jsonb", name = "client_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserClientConfig clientConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User<?> user = (User<?>) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

