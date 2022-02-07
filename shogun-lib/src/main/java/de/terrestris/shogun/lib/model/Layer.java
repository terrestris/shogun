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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.geojson.GeoJsonObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity(name = "layers")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "layers_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "layers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Layer extends BaseEntity {

    @Column(nullable = false)
    @Schema(
        description = "The internal name of the layer.",
        example = "MySHOGunLayer"
    )
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The configuration of the layer which should be used to define client specific aspects of " +
            "the layer. This may include the name, the visible resolution range, search configurations or similar."
    )
    private LayerClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The configuration of the datasource of the layer, e.g. the URL of the server, the name or " +
            "the grid configuration.",
        required = true
    )
    private LayerSourceConfig sourceConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "Custom features for the layers that aren't available in the datasource. This might be used " +
            "for custom draw layers or similiar. It's advised to store the features using the GeoJSON format."
    )
    private GeoJsonObject features;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(
        description = "The type of the layer. Currently one of `TileWMS`, `VectorTile`, `WFS`, `WMS`, `WMTS` or `XYZ`.",
        required = true
    )
    private LayerType type;
}
