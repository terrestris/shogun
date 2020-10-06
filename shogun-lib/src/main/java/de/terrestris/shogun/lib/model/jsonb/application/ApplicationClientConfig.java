package de.terrestris.shogun.lib.model.jsonb.application;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ApplicationClientConfig implements Serializable {
    private String logoPath;
    private Map<String, Object> mapView;
}
