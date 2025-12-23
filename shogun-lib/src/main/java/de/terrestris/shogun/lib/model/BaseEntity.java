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

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@MappedSuperclass
@Audited
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseEntity implements Serializable {

    // TODO Replace with @GeneratedValue(strategy = GenerationType.IDENTITY) and remove hibernate_sequence from flyway migrations
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column
    @Getter @Setter
    private OffsetDateTime modified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
