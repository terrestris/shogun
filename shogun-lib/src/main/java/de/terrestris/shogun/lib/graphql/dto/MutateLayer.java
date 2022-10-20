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
public class MutateLayer implements Serializable {
    private String name;
    private HashMap<String, Object> clientConfig;
    private HashMap<String, Object> sourceConfig;
    private HashMap<String, Object> features;
    private String type;
}
