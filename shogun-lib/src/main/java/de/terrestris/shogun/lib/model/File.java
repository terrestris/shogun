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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "files")
@Table(schema = "shogun")
@DynamicUpdate
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "files")
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class File extends BaseEntity {

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @Type(type="pg-uuid")
    @Getter
    @Schema(
        description = "The (auto assigned) UUID of the file.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID fileUuid = UUID.randomUUID();

    @Column
    @Getter @Setter
    @Schema(
        description = "Whether the file is considered as active or not.",
        example = "true"
    )
    private Boolean active;

    @Column(nullable = false)
    @Getter @Setter
    @Schema(
        description = "The (original) name of the file.",
        example = "shogun.png"
    )
    private String fileName;

    @Column(nullable = false)
    @Getter @Setter
    @Schema(
        description = "The (original) type of the file.",
        example = "image/png"
    )
    private String fileType;

    @JsonIgnore
    @ToString.Exclude
    @Column(length = Integer.MAX_VALUE)
    @Getter @Setter
    private byte[] file;

    @JsonIgnore
    @Column
    @Getter @Setter
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        File file = (File) o;
        return getId() != null && Objects.equals(getId(), file.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
