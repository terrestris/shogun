package de.terrestris.shogun.lib.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class TextualContentDto extends BaseEntityDto {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public abstract static class Base extends ReadOnlyFieldsBase {
        @NotBlank(message = "The auth provider is required")
        @Schema(
            description = "The category of the textual content.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String category;

        @Schema(
            description = "The title of the textual content.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String title;

        @Schema(
            description = "The textual content.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String markdown;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "TextualContentCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "TextualContentUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(
        name = "TextualContentRead",
        description = "DTO for reading an Application"
    )
    public static class Read extends Base { }
}
