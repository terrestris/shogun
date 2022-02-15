package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.LayerConfig;
import de.terrestris.shogun.lib.model.jsonb.layer.DefaultLayerClientConfig;
import de.terrestris.shogun.lib.model.jsonb.layer.DefaultLayerSourceConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@JsonDeserialize(as = DefaultApplicationLayerConfig.class)
@JsonSuperType(type = LayerConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultApplicationLayerConfig implements LayerConfig {
    @Schema(
        description = "The configuration of the layer which should be used to define client specific aspects of " +
            "the layer. This may include the name, the visible resolution range, search configurations or similar."
    )
    private DefaultLayerClientConfig clientConfig;

    @Schema(
        description = "The configuration of the datasource of the layer, e.g. the URL of the server, the name or " +
            "the grid configuration."
    )
    private DefaultLayerSourceConfig sourceConfig;
}
