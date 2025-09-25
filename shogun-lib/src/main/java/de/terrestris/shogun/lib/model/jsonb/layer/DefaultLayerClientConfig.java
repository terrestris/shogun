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
import de.terrestris.shogun.lib.model.jsonb.LayerClientConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@JsonDeserialize(as = DefaultLayerClientConfig.class)
@JsonSuperType(type = LayerClientConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class DefaultLayerClientConfig implements LayerClientConfig {

    @Schema(
        description = "The minimum resolution of the layer (at what resolution/zoom level the layer should become visible).",
        example = "305.74811309814453"
    )
    private Double minResolution;

    @Schema(
        description = "The maximum resolution of the layer (to which resolution/zoom level the layer should be visible).",
        example = "2500"
    )
    private Double maxResolution;

    @Schema(
        description = "Whether the layer is hoverable or not.",
        example = "true"
    )
    private Boolean hoverable;

    @Schema(
        description = "Whether the layer is searchable or not.",
        example = "true"
    )
    private Boolean searchable;

    @Schema(
        description = "List of download configurations."
    )
    private ArrayList<DownloadConfig> downloadConfig;

    @Schema(
        description = "The search configuration."
    )
    private SearchConfig searchConfig;

    @Schema(
        description = "The shared property configuration that should be used application wide."
    )
    private ArrayList<DefaultLayerPropertyConfig> propertyConfig;

    @Schema(
        description = "The configuration for the feature info form/view."
    )
    private ArrayList<PropertyFormTabConfig<PropertyFormItemReadConfig>> featureInfoFormConfig;

    @Schema(
        description = "The configuration for the feature edit form."
    )
    private ArrayList<PropertyFormTabConfig<PropertyFormItemEditConfig>> editFormConfig;

    @Schema(
        description = "The cross Origin mode to use.",
        example = "anonymous"
    )
    private String crossOrigin;

    @Schema(
        description = "The default opacity of the layer.",
        example = "1"
    )
    private Float opacity;

    @Schema(
        description = "Whether the layer is editable or not.",
        example = "true"
    )
    private Boolean editable;

    @Schema(
        description = "The url to the associated SLD style document.",
        example = "http://localhost/geoserver/ows?service=WMS&request=GetStyles&version=1.1.1&layers=SHOGUN:COUNTRIES"
    )
    private String styleUrl;
}
