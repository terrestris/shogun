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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.ApplicationToolConfig;
import de.terrestris.shogun.lib.model.jsonb.application.tool.DefaultTool;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@JsonDeserialize(as = DefaultApplicationToolConfig.class)
@JsonSuperType(type = ApplicationToolConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "name",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true,
    defaultImpl = ToolConfig.class,
    requireTypeIdForSubtypes = OptBoolean.FALSE
)
//@JsonSubTypes({
//    @JsonSubTypes.Type(
//        value = FeatureInfoToolConfig.class,
//        name = "feature_Info"
//    ),
//    @JsonSubTypes.Type(
//        value = ToolConfig.class,
//        names = {
//            "measure_tools",
//            "measure_tools_distance",
//            "measure_tools_area",
//            "draw_tools",
//            "draw_tools_point",
//            "draw_tools_line",
//            "draw_tools_polygon",
//            "draw_tools_circle",
//            "draw_tools_rectangle",
//            "draw_tools_annotation",
//            "draw_tools_modify",
//            "draw_tools_upload",
//            "draw_tools_download",
//            "draw_tools_delete",
//            "draw_tools_style",
//            "print",
//            "tree",
//            "permalink",
//            "language_selector",
//            "search",
//
//            "hclim-feature-info-plugin",
//            "diagram-plugin"
//        }
//    )
//})
//@Schema(
//    discriminatorProperty = "name",
//    discriminatorMapping = {
//        @DiscriminatorMapping(
//            value = "feature_Info",
//            schema = FeatureInfoToolConfig.class
//        ),
//        @DiscriminatorMapping(
//            value = "measure_tools",
//            schema = ToolConfig.class
//        ),
//        @DiscriminatorMapping(
//            value = "draw_tools",
//            schema = ToolConfig.class
//        )
//    }
//)
public class DefaultApplicationToolConfig<T extends DefaultTool> implements ApplicationToolConfig {

    @Schema(
        description = "The name of the tool.",
        example = "map-tool",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NonNull
    private String name;

    @Schema(
        description = "The configuration object of the tool.",
        example = "{\"visible\": true}"
    )
    private T config;
}
