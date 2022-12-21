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
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
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
            "the layer. This may include the name, the visible resolution range, search configurations or similar.",
        example = "[\n" +
            "{\n" +
              "\"layerId\": 2,\n" +
              "\"clientConfig\": {\n" +
                "\"opacity\": 30\n" +
              "}\n" +
            "}\n" +
          "]\n"
    )
    private LayerClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The configuration of the datasource of the layer, e.g. the URL of the server, the name or " +
            "the grid configuration.",
        required = true,
        example = "{\n" +
            "\"url\": \"https://ows.terrestris.de/osm/service?\",\n" +
            "\"layerNames\": \"OSM-WMS\",\n" +
            "\"useBearerToken\": false\n" +
          "}\n"
    )
    private LayerSourceConfig sourceConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "Custom features for the layers that aren't available in the datasource. This might be used " +
            "for custom draw layers or similiar. It's advised to store the features using the GeoJSON format.",
        example = "{\n" +
            "\"type\": \"FeatureCollection\",\n" +
            "\"features\": [\n" +
              "{\n" +
                "\"type\": \"Feature\",\n" +
                "\"geometry\": {\n" +
                  "\"type\": \"Point\",\n" +
                  "\"coordinates\": [102.0, 0.5]\n" +
                "},\n" +
                "\"properties\": {\n" +
                  "\"prop0\": \"value0\"\n" +
                "}\n" +
              "},\n" +
              "{\n" +
                "\"type\": \"Feature\",\n" +
                "\"geometry\": {\n" +
                  "\"type\": \"LineString\",\n" +
                  "\"coordinates\": [\n" +
                    "[102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]\n" +
                  "]\n" +
                "},\n" +
                "\"properties\": {\n" +
                  "\"prop0\": \"value0\",\n" +
                  "\"prop1\": 0.0\n" +
                "}\n" +
              "},\n" +
              "{\n" +
                "\"type\": \"Feature\",\n" +
                "\"geometry\": {\n" +
                  "\"type\": \"Polygon\",\n" +
                  "\"coordinates\": [\n" +
                    "[\n" +
                      "[100.0, 0.0], [101.0, 0.0], [101.0, 1.0],\n" +
                      "[100.0, 1.0], [100.0, 0.0]\n" +
                    "]\n" +
                  "]\n" +
                "},\n" +
                "\"properties\": {\n" +
                  "\"prop0\": \"value0\",\n" +
                  "\"prop1\": { \"this\": \"that\" }\n" +
                "}\n" +
              "}\n" +
            "]\n" +
          "}\n"
    )
    private GeoJsonObject features;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(
        description = "The type of the layer. Currently one of `TileWMS`, `VectorTile`, `WFS`, `WMS`, `WMTS` or `XYZ`.",
        required = true
    )
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
