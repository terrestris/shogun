package de.terrestris.shogun.lib.dto.model;

import de.terrestris.shogun.lib.enumeration.LayerType;
import de.terrestris.shogun.lib.model.jsonb.LayerClientConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerSourceConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.geojson.GeoJsonObject;

public class LayerDto extends BaseEntityDto {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public abstract static class Base extends ReadOnlyFieldsBase {
        @NotBlank(message = "The name is required")
        @Schema(
            description = "The internal name of the layer.",
            example = "MySHOGunLayer"
        )
        private String name;

        @Schema(
            description = "The configuration of the layer which should be used to define client specific aspects of " +
                "the layer. This may include the name, the visible resolution range, search configurations or similar."
        )
        private LayerClientConfig clientConfig;

        @Schema(
            description = "The configuration of the datasource of the layer, e.g. the URL of the server, the name or " +
                "the grid configuration.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private LayerSourceConfig sourceConfig;

        @Schema(
            description = "Custom features for the layers that aren't available in the datasource. This might be used " +
                "for custom draw layers or similar. It's advised to store the features using the GeoJSON format."
        )
        private GeoJsonObject features;

        @Schema(
            description = "The type of the layer. Currently one of `TileWMS`, `VectorTile`, `WFS`, `WMS`, `WMTS` or `XYZ`.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private LayerType type;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "LayerCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "LayerUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "LayerRead",
        description = "DTO for updating an existing Application"
    )
    public static class Read extends Base { }
}
