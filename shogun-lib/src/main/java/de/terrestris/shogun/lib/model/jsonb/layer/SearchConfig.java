/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2023-present terrestris GmbH & Co. KG
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

import com.fasterxml.jackson.annotation.JsonInclude;
import de.terrestris.shogun.lib.model.jsonb.layer.PropertyFormTabConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class SearchConfig implements Serializable {

    @Schema(
        description = "List of attributes which will be considered when searching. When empty, all attributes will be searched.",
        example = "[\"name\", \"street\", \"postcode\"]"
    )
    private List<String> attributes;

    @Schema(
        description = "Search display template.",
        example = "{name}"
    )
    private String displayTemplate;

    @Schema(
        description = "The configuration for displaying search results in the result drawer.",
        example = "{\"title\":\"{name}\",\"children\":[{\"propertyName\":\"link\",\"displayName\":\"Details\",\"fieldProps\":{\"urlDisplayValue\":\"Click here\"}}]}"
    )
    // TODO: We should consider to create a generetic type for this and other PropertyForm Configs
    private PropertyFormTabConfig<PropertyFormItemReadConfig> resultDrawerConfig;
}
