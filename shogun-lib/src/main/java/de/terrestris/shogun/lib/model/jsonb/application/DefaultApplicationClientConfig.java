package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.ApplicationClientConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@JsonDeserialize(as = DefaultApplicationClientConfig.class)
@JsonSuperType(type = ApplicationClientConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultApplicationClientConfig implements ApplicationClientConfig {
    @Schema(
        description = "The configuration of the map view.",
        required = true
    )
    private DefaultMapView mapView;

    @Schema(
        description = "The description of the application."
    )
    private String description;
}
