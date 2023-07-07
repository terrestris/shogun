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
package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.LayerConfig;
import de.terrestris.shogun.lib.model.jsonb.layer.DefaultLayerClientConfig;
import de.terrestris.shogun.lib.model.jsonb.layer.DefaultLayerSourceConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@JsonDeserialize(as = DefaultApplicationLayerConfig.class)
@JsonSuperType(type = LayerConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class DefaultApplicationLayerConfig implements LayerConfig {

    @Schema(
        description = "The ID of the layer to apply the custom configuration to.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NonNull
    private Integer layerId;

    @Schema(
        description = "The configuration of the layer which should be used to define client specific aspects of " +
            "the layer. This may include the name, the visible resolution range, search configurations or similar."
    )
    private DefaultLayerClientConfig clientConfig;

    @Schema(
        description = "The configuration of the datasource of the layer, e.g. the URL of the server, the name or " +
            "the grid configuration."
    )
    private DefaultLayerSourceConfig sourceConfig;
}
