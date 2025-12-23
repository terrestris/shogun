package de.terrestris.shogun.lib.dto.model;

import de.terrestris.shogun.lib.model.jsonb.UserClientConfig;
import de.terrestris.shogun.lib.model.jsonb.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class UserDto extends BaseEntityDto{

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
            description = "The user details stored in the associated provider.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        private T providerDetails;

        @Schema(
            description = "Custom user details that aren't stored inside the provider."
        )
        private UserDetails details;

        @Schema(
            description = "The configuration of the user which should be used to define client specific aspects of " +
                "the user. This may include the locale set by the user, the last application visited by the user or similar."
        )
        private UserClientConfig clientConfig;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "UserCreate",
        description = "DTO for creating a new Application"
    )
    public static class Create extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "UserUpdate",
        description = "DTO for updating an existing Application"
    )
    public static class Update extends Base { }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(
        name = "UserRead",
        description = "DTO for updating an existing Application"
    )
    public static class Read extends Base { }
}
