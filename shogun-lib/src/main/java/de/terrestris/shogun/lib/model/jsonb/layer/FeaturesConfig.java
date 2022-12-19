/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.model.jsonb.layer;

import de.terrestris.shogun.lib.annotation.JsonSuperType;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
public class FeaturesConfig implements Serializable {

    @Schema(
        description = "The object type",
        example = "Feature",
        required = true
    )
    private String type;

    @Schema(
        description = "The definition of the feature geometry"
    )
    private GeometryConfig geometry;

    @Schema(
        description = "The properties associated to a feature",
        example = "\"{\"prop0\": \"value0\"}\""
    )
    private HashMap<String,Object> properties;
}
