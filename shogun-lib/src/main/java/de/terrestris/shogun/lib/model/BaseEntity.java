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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
@Audited
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class BaseEntity implements Serializable {

    // TODO Replace with @GeneratedValue(strategy = GenerationType.IDENTITY) and remove hibernate_sequence from flyway migrations
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    @Schema(
        description = "The ID of the entity.",
        accessMode = Schema.AccessMode.READ_ONLY,
        readOnly = true
    )
    private Long id;

    @Column(columnDefinition = "uuid")
    @Type(type="pg-uuid")
    @Getter @Setter
    @Schema(
        description = "The UUID of the entity."
    )
    private UUID uuid = UUID.randomUUID();

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    @Schema(
        description = "The timestamp of creation.",
        accessMode = Schema.AccessMode.READ_ONLY,
        readOnly = true
    )
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column
    @Getter @Setter
    @Schema(
        description = "The timestamp of the last modification.",
        accessMode = Schema.AccessMode.READ_ONLY,
        readOnly = true
    )
    private OffsetDateTime modified;

}
