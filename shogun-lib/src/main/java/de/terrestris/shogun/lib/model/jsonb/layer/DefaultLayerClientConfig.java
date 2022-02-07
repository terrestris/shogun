package de.terrestris.shogun.lib.model.jsonb.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.LayerClientConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Data
@JsonDeserialize(as = DefaultLayerClientConfig.class)
@JsonSuperType(type = LayerClientConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
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
        description = "The search configuration."
    )
    private Map<String, Object> searchConfig;

    @Schema(
        description = "The property configuration."
    )
    private ArrayList<DefaultLayerPropertyConfig> propertyConfig;

    @Schema(
        description = "The cross Origin mode to use.",
        example = "anonymous"
    )
    private String crossOrigin;
}

