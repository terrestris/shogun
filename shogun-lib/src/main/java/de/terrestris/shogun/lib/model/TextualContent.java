/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2025-present terrestris GmbH & Co. KG
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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity(name = "textualcontents")
@Table(schema = "shogun")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "textualcontents")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Audited
@AuditTable(value = "textualcontents_rev", schema = "shogun_rev")
@Schema(
    description = "The textual content entity holds markdown content along with its category and title, used " +
        "for various informational purposes within the application."
)
public class TextualContent extends BaseEntity {

    @Column(nullable = false)
    @Schema(
        description = "The category of the textual content, e.g., NEWS, INFO, etc.",
        example = "NEWS",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String category;

    @Column(nullable = false)
    @Schema(
        description = "The title of the textual content.",
        example = "Breaking news",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @Column(nullable = false)
    @Schema(
        description = "The content of the textual content as markdown formatted text.",
        example = "# Breaking news \n This is the **content** of the news.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String markdown;

}
