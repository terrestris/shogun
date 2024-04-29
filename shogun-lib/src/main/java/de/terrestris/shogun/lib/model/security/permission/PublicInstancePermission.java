/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity(name = "publicinstancepermissions")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "publicinstancepermissions_rev", schema = "shogun_rev")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class PublicInstancePermission implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    @Schema(
        description = "The ID.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(unique = true, nullable = false)
    private Long entityId;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    @Schema(
        description = "The timestamp of creation.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column
    @Getter @Setter
    @Schema(
        description = "The timestamp of the last modification.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private OffsetDateTime modified;

}
