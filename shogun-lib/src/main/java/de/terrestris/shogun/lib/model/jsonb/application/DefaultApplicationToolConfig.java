package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.ApplicationToolConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;

@Data
@JsonDeserialize(as = DefaultApplicationToolConfig.class)
@JsonSuperType(type = ApplicationToolConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultApplicationToolConfig implements ApplicationToolConfig {
    @Schema(
        description = "The name of the tool.",
        example = "map-tool"
    )
    private String name;

    @Schema(
        description = "The configuration object of the tool.",
        example = "{\"visible\": true}"
    )
    private HashMap<String, Object> config;
}
