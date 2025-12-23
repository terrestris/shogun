package de.terrestris.shogun.lib.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class GroupDto extends BaseEntityDto {

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
            description = "The group details stored in the associated Keycloak entity.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        private T providerDetails;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "GroupCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "GroupUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "GroupRead",
        description = "DTO for updating an existing Application"
    )
    public static class Read extends Base { }
}
