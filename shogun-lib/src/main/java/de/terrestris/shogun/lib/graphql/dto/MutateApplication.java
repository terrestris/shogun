package de.terrestris.shogun.lib.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MutateApplication implements Serializable {
    private String name;
    private Boolean stateOnly;
    private HashMap<String, Object> clientConfig;
    private HashMap<String, Object> layerTree;
    private HashMap<String, Object> layerConfig;
    private HashMap<String, Object> toolConfig;
    private String type;
}
