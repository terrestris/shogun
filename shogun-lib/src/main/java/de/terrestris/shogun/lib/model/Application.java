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

import de.terrestris.shogun.lib.model.jsonb.ApplicationClientConfig;
import de.terrestris.shogun.lib.model.jsonb.ApplicationToolConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "applications")
@Table(schema = "shogun")
@DynamicUpdate
@Audited
@AuditTable(value = "applications_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="applications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Application extends BaseEntity {

    @Column
    @Schema(
        description = "The name of the application.",
        required = true,
        example = "My SHOGun application"
    )
    private String name;

    @Column
    @Schema(
        description = "Whether the application configuration is considered as state or not. A state may be used " +
            "as snapshot of a given application.",
        example = "false"
    )
    private Boolean stateOnly;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The configuration to be considered by the client/application which may include all specific " +
            "configurations required by the project.",
        example = "{\n" +
            "\"mapView\": {\n" +
              "\"zoom\": 2,\n" +
              "\"center\": [10, 51], \n" +
              "\"extent\": null,\n" +
              "\"projection\": \"EPSG:3857\",\n" +
              "\"resolutions\": [\n" +
                "8920,\n" +
                "4480,\n" +
                "2240,\n" +
                "1120,\n" +
                "560,\n" +
                "350,\n" +
                "280,\n" +
                "140,\n" +
                "70,\n" +
                "28,\n" +
                "14,\n" +
                "7,\n" +
                "2.8,\n" +
                "1.4,\n" +
                "0.7,\n" +
                "0.28,\n" +
                "0.07\n" +
              "]\n" +
            "},\n" +
            "\"description\": \"An awesome shogun app\",\n" +
            "\"theme\": {\n" +
              "\"primaryColor\": \"#02203d\",\n" +
              "\"secondaryColor\": \"#73b3fb\",\n" +
              "\"complementaryColor\": \"#ffffff\",\n" +
              "\"logoPath\": \"https://terrestris.github.io/fossgis2019/shared/img/shogun-qgis-talk/logo-shogun.png\"\n" +
            "}\n" +
          "}\n"
    )
    private ApplicationClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The tree shaped configuration entry of the applications table of contents.",
        example = "{\n" +
            "\"title\": \"root\",\n" +
            "\"children\": [\n" +
              "{\n" +
                "\"title\": \"Backgroundlayer\",\n" +
                "\"checked\": true,\n" +
                "\"children\": [\n" +
                  "{\n" +
                    "\"title\": \"OSM\",\n" +
                    "\"checked\": false,\n" +
                    "\"layerId\": 1\n" +
                  "},\n" +
                  "{\n" +
                    "\"title\": \"OSM-WMS\",\n" +
                    "\"checked\": false,\n" +
                    "\"layerId\": 2\n" +
                  "},\n" +
                  "{\n" +
                    "\"title\": \"OSM-WMS (gray)\",\n" +
                    "\"checked\": true,\n" +
                    "\"layerId\": 3\n" +
                  "}\n" +
                "]\n" +
              "},\n" +
              "{\n" +
                "\"title\": \"Countries\",\n" +
                "\"checked\": false,\n" +
                "\"layerId\": 4\n" +
             "}\n" +
            "]\n" +
          "}\n"
    )
    private LayerTree layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The definition of layer configurations. This may be used to set application specific " +
            "configurations for any layers in the given application.",
        example = "[\n" +
            "{\n" +
              "\"layerId\": 2,\n" +
              "\"clientConfig\": {\n" +
                "\"opacity\": 30\n" +
              "}\n" +
            "}\n" +
          "]\n"
    )
    private List<LayerConfig> layerConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The definition of tool configurations. This may be used to set application specific " +
            "configurations for any tools in the given application, e.g. the visibility or the layers the tool should work on.",
        example = "[\n" +
            "{\n" +
              "\"name\": \"measure_tools\",\n" +
              "\"config\": {\n" +
                "\"visible\": true\n" +
              "}\n" +
            "}\n" +
          "]\n"
    )
    private List<ApplicationToolConfig> toolConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Application that = (Application) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
