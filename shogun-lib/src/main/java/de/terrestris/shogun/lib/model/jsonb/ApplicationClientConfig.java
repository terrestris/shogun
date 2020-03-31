package de.terrestris.shogun.lib.model.jsonb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationClientConfig implements Serializable {
    private String logoPath;
    private Map<String, Object> mapView;
}
