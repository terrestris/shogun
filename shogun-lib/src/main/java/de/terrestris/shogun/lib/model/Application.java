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
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

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
        requiredMode = Schema.RequiredMode.REQUIRED,
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

    @Column(columnDefinition = "jsonb", name = "client_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The configuration to be considered by the client/application which may include all specific " +
            "configurations required by the project."
    )
    private ApplicationClientConfig clientConfig;

    @Column(columnDefinition = "jsonb", name = "layer_tree")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The tree shaped configuration entry of the applications table of contents."
    )
    private LayerTree layerTree;

    @Column(columnDefinition = "jsonb", name = "layer_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The definition of layer configurations. This may be used to set application specific " +
            "configurations for any layers in the given application."
    )
    private List<LayerConfig> layerConfig;

    @Column(columnDefinition = "jsonb", name = "tool_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Schema(
        description = "The definition of tool configurations. This may be used to set application specific " +
            "configurations for any tools in the given application, e.g. the visibility or the layers the tool should work on."
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
