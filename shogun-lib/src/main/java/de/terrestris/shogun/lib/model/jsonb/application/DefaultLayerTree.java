package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.LayerTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;

@Data
@JsonDeserialize(as = DefaultLayerTree.class)
@JsonSuperType(type = LayerTree.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultLayerTree implements LayerTree {
    @Schema(
        description = "The title of the node to show.",
        example = "Layer A"
    )
    private String title;

    @Schema(
        description = "Whether the node should be checked initially or not.",
        example = "true"
    )
    private Boolean checked;

    @Schema(
        description = "The ID of the layer to associate to the node.",
        example = "1909"
    )
    private Integer layerId;

    @Schema(
        description = "The children configuration",
        example = "[]"
    )
    private ArrayList<DefaultLayerTree> children;
}

