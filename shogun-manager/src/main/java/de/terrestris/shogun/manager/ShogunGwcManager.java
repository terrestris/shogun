package de.terrestris.shogun.manager;

import de.terrestris.shogun.manager.gwc.GwcSeedRequest;
import de.terrestris.shogun.manager.gwc.SeedingTaskStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class ShogunGwcManager extends AbstractShogunManager {

    private final String SEEDING_ENDPOINT = "/seed/";

    public ShogunGwcManager(String adminUser, String adminPassword, String serviceBaseUrl) {
        super(adminUser, adminPassword, serviceBaseUrl);
    }

    /**
     * Trigger (re-)seed / truncate for certain layer
     * @param gwcSeedRequest The GWC reseed object (instance of {@link GwcSeedRequest})
     * @return true if seeding / truncating was triggered successfully
     * @throws Exception Exception on error
     */
    public boolean startSeeding(GwcSeedRequest gwcSeedRequest) throws Exception {
        Map<String, Object> payLoad = gwcSeedRequest.getPayLoad();
        String qualifiedLayerName = gwcSeedRequest.getQualifiedLayerName();
        if (StringUtils.isEmpty(qualifiedLayerName)) {
            throw new Exception("Qualified layer name is not provided");
        }
        HttpPost request = new HttpPost(new URI(String.format("%s%s%s.json", this.serviceBaseUrl, SEEDING_ENDPOINT, qualifiedLayerName)));
        StringEntity stringEntity = new StringEntity(objectMapper.writeValueAsString(payLoad), ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);

        byte[] resultBytes = performRequest(request);
        if (resultBytes != null) {
            return true;
        } else {
            throw new IOException("Could not read interceptor rules from backend");
        }
    }

    /**
     * Return List of {@link SeedingTaskStatus} for given layer
     * @param qualifiedLayerName The qualified layer name
     * @return List of {@link SeedingTaskStatus}
     * @throws Exception Exception
     */
    public List<SeedingTaskStatus> getRunningTasksForLayer(String qualifiedLayerName) throws Exception {
        if (StringUtils.isEmpty(qualifiedLayerName)) {
            throw new Exception("Qualified layer name is not provided");
        }
        HttpGet getRequest = new HttpGet(new URI(String.format("%s%s%s.json", this.serviceBaseUrl, SEEDING_ENDPOINT, qualifiedLayerName)));
        byte[] resultBytes = performRequest(getRequest);
        if (resultBytes != null) {
            Map<String, ArrayList<ArrayList<Integer>>> resultMap = objectMapper.readValue(resultBytes, Map.class);
            return resultMap.get("long-array-array").stream().map(stringArrayListEntry -> new SeedingTaskStatus(
                stringArrayListEntry.get(0),
                stringArrayListEntry.get(1),
                stringArrayListEntry.get(2),
                stringArrayListEntry.get(3),
                stringArrayListEntry.get(4)
            )).collect(Collectors.toList());
        } else {
            throw new IOException(String.format("Could not tasks for layer %s from GWC", qualifiedLayerName));
        }
    }
}
}
