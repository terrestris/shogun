package de.terrestris.shogun.lib.model.jsonb.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultLayerPropertyConfig implements Serializable {

    @Schema(
        description = "The name of the property.",
        example = "description",
        required = true
    )
    private String propertyName;

    @Schema(
        description = "The name of the attribute to show.",
        example = "Description"
    )
    private String displayName;

    @Schema(
        description = "Whether the attribute should be shown or not.",
        example = "true"
    )
    private boolean visible = true;
}

