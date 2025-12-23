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
import io.swagger.v3.oas.annotations.Hidden;
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
@Hidden
public class Application extends BaseEntity {

    @Column
    private String name;

    @Column
    private Boolean stateOnly;

    @Column(columnDefinition = "jsonb", name = "client_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private ApplicationClientConfig clientConfig;

    @Column(columnDefinition = "jsonb", name = "layer_tree")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private LayerTree layerTree;

    @Column(columnDefinition = "jsonb", name = "layer_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LayerConfig> layerConfig;

    @Column(columnDefinition = "jsonb", name = "tool_config")
    @Type(JsonBinaryType.class)
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
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
