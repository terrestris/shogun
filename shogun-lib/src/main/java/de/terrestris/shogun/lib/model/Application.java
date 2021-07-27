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

import de.terrestris.shogun.lib.model.jsonb.ApplicationToolConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerTree;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity(name = "applications")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "applications_rev", schema = "shogun_rev")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Application extends BaseEntity {

    @Column
    private String name;

    @Column
    private Boolean stateOnly;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private ApplicationClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private LayerTree layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LayerConfig> layerConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ApplicationToolConfig> toolConfig;
}
