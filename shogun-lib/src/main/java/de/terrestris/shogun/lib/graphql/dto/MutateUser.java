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
public class MutateUser implements Serializable {
    private String authProviderId;
    private HashMap<String, Object> details;
    private HashMap<String, Object> clientConfig;
}
