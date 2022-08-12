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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.LayerSourceConfig;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@JsonDeserialize(as = DefaultLayerSourceConfig.class)
@JsonSuperType(type = LayerSourceConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultLayerSourceConfig implements LayerSourceConfig {
    @Schema(
        description = "The base URL of the layer.",
        example = "https://ows.terrestris.de/ows"
    )
    private String url;

    @Schema(
        description = "A comma separated list of layers to request.",
        example = "layerA"
    )
    private String layerNames;

    @Schema(
        description = "A custom legend URL.",
        example = "https://ows.terrestris.de/ows/my-legend.png"
    )
    private String legendUrl;

    @Schema(
        description = "The tile size.",
        example = "512"
    )
    private Double tileSize;

    @Schema(
        description = "The tile origin.",
        example = "[239323.44497139292, 4290144.074117256]"
    )
    private ArrayList<Double> tileOrigin;

    @Schema(
        description = "The matrix set identifier, which should be used with the WMTS layer source.",
        example = "WEBMERCATOR"
    )
    private String matrixSet;

    @Schema(
        description = "The list of resolutions the layer should be requested on.",
        example = "[2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508]"
    )
    private ArrayList<Double> resolutions;

    @Schema(
        description = "The attribution to show.",
        example = "© by terrestris GmbH & Co. KG"
    )
    private String attribution;

    @ApiModelProperty(
        value = "Request parameters to be passed to the service when querying a layer.",
        example = "{\"transparent\": true}",
        dataType = "object"
    )
    private HashMap<String, Object> requestParams;

    @Schema(
        description = "Whether to send the (internal) Keycloak Bearer Token alongside the map requests or not. " +
            "Please note: This should be set to `true` for internal layers only!",
        example = "false"
    )
    private Boolean useBearerToken;
}

