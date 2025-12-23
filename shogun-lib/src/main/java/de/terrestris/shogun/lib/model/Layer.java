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

import de.terrestris.shogun.lib.enumeration.LayerType;
import de.terrestris.shogun.lib.model.jsonb.LayerClientConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerSourceConfig;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.geojson.GeoJsonObject;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity(name = "layers")
@Table(schema = "shogun")
@DynamicUpdate
@Audited
@AuditTable(value = "layers_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "layers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Layer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "jsonb", name = "client_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private LayerClientConfig clientConfig;

    @Column(columnDefinition = "jsonb", name = "source_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private LayerSourceConfig sourceConfig;

    @Column(columnDefinition = "jsonb", name = "features")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private GeoJsonObject features;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LayerType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Layer layer = (Layer) o;
        return getId() != null && Objects.equals(getId(), layer.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
