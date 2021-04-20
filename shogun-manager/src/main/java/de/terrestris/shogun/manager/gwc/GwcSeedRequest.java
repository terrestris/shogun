package de.terrestris.shogun.manager.gwc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Envelope;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GwcSeedRequest {

    private String qualifiedLayerName;
    private Envelope bounds;
    private int srsCode;
    private String gridSetId;
    private int zoomStart = 0;
    private int zoomStop;
    private String format = "image/png";
    private SeedingType type;
    private int threadCount = 1;

    public Map<String, Object> getPayLoad() {
        Map<String, Object> seedRequest = new HashMap<>();
        seedRequest.put("name", qualifiedLayerName);
        seedRequest.put("gridSetId", gridSetId);
        Map<String, Integer> srsMap = new HashMap<>();
        srsMap.put("number", srsCode);
        seedRequest.put("srs", srsMap);
        seedRequest.put("zoomStop", zoomStop);
        seedRequest.put("zoomStart", zoomStart);
        seedRequest.put("format", format);
        seedRequest.put("type", type);
        seedRequest.put("threadCount", threadCount);
        Map<String, Map<String, double[]>> boundsMap = new HashMap<>();
        Map<String, double[]> doubleOrdsMap = new HashMap<>();
        doubleOrdsMap.put("double", new double[]{bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()});
        boundsMap.put("coords", doubleOrdsMap);
        seedRequest.put("bounds", boundsMap);

        Map<String, Object> returnObj = new HashMap<>();
        returnObj.put("seedRequest", seedRequest);
        return returnObj;
    }

}

