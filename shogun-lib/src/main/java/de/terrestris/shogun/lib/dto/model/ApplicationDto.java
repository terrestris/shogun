package de.terrestris.shogun.lib.dto.model;

import de.terrestris.shogun.lib.model.jsonb.ApplicationClientConfig;
import de.terrestris.shogun.lib.model.jsonb.ApplicationToolConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerConfig;
import de.terrestris.shogun.lib.model.jsonb.LayerTree;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class ApplicationDto extends BaseEntityDto {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public abstract static class Base extends ReadOnlyFieldsBase {
        @NotBlank(message = "Application name is required")
        @Schema(
            description = "The name of the application.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "My SHOGun application"
        )
        private String name;

        @Schema(
            description = "Whether the application configuration is considered as state or not. A state may be used " +
                "as snapshot of a given application.",
            example = "false"
        )
        private Boolean stateOnly;

        @Schema(
            description = "The configuration to be considered by the client/application which may include all specific " +
                "configurations required by the project."
        )
        private ApplicationClientConfig clientConfig;

        @Schema(
            description = "The tree shaped configuration entry of the applications table of contents."
        )
        private LayerTree layerTree;

        @Schema(
            description = "The tree shaped configuration entry of the applications table of contents."
        )
        private List<LayerConfig> layerConfig;

        @Schema(
            description = "The tree shaped configuration entry of the applications table of contents."
        )
        private List<ApplicationToolConfig> toolConfig;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "ApplicationCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "ApplicationUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "ApplicationRead",
        description = "DTO for reading an Application"
    )
    public static class Read extends Base { }
}
