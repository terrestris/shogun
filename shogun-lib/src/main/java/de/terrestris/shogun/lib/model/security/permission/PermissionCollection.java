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
package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "permissions")
@Table(schema = "shogun")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "permissions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PermissionCollection extends BaseEntity {

    @ElementCollection
    @CollectionTable(name="permission", schema = "shogun")
    @Enumerated(EnumType.STRING)
    @Fetch(FetchMode.JOIN)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "permission")
    private Set<PermissionType> permissions = new HashSet<>();

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionCollectionType name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PermissionCollection that = (PermissionCollection) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
