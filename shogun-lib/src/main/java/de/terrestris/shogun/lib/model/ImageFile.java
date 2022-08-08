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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "imagefiles")
@Table(schema = "shogun")
@Cacheable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ImageFile extends File {

    @Column
    @Schema(
        description = "The (original) width of the image file.",
        example = "100"
    )
    private Integer width;

    @Column
    @Schema(
        description = "The (original) height of the image file.",
        example = "100"
    )
    private Integer height;

    @JsonIgnore
    @ToString.Exclude
    @Column(length = Integer.MAX_VALUE)
    private byte[] thumbnail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ImageFile imageFile = (ImageFile) o;
        return getId() != null && Objects.equals(getId(), imageFile.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
