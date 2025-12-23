package de.terrestris.shogun.lib.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class RoleDto extends BaseEntityDto {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public abstract static class Base<T> extends ReadOnlyFieldsBase {
        @NotBlank(message = "The auth provider is required")
        @Schema(
            description = "The backend ID of the user.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String authProviderId;

        @Schema(
            description = "The role details stored in the associated provider.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        private T providerDetails;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "RoleCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "RoleUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "RoleRead",
        description = "DTO for updating an existing Application"
    )
    public static class Read extends Base { }
}
