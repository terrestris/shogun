package de.terrestris.shogun.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.lib.enumeration.RuleType;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * The SHOGun-GeoServer-Interceptor manager
 */
@Log4j2
public class ShogunGsInterceptorManager extends AbstractShogunManager {

    final String basePathInterceptorRule = "/interceptorrules";

    public ShogunGsInterceptorManager(String adminUser, String adminPassword, String shogunBaseUrl) {
        super(adminUser, adminPassword, shogunBaseUrl);
    }

    /**
     * Return list of all available interceptor rules from SHOGun gs-interceptor
     * @return List of all {@link InterceptorRule}s
     * @throws Exception The exception
     */
    public List<InterceptorRule> getAllInterceptorRules() throws Exception {
        HttpGet getRequest = new HttpGet(new URI(String.format("%s%s", this.shogunServiceBaseUrl, basePathInterceptorRule)));
        byte[] resultBytes = performRequest(getRequest);
        if (resultBytes != null) {
            return objectMapper.readValue(resultBytes, new TypeReference<>() {});
        } else {
            throw new IOException("Could not read interceptor rules from backend");
        }
    }

    /**
     * Return list of interceptor rules from SHOGun gs-interceptor matching passed {@link de.terrestris.shogun.interceptor.enumeration.OgcEnum.ServiceType}
     * and {@link de.terrestris.shogun.interceptor.enumeration.HttpEnum.EventType}
     * @param service The {@link de.terrestris.shogun.interceptor.enumeration.OgcEnum.ServiceType} to search for
     * @param event The {@link de.terrestris.shogun.interceptor.enumeration.HttpEnum.EventType} to search for
     * @return List of matching {@link InterceptorRule}s
     * @throws Exception The exception
     */
    public List<InterceptorRule> findAllRulesForServiceAndEvent(OgcEnum.ServiceType service, HttpEnum.EventType event) throws Exception {
        String urlString = String.format("%s%s/service/%s/event/%s", this.shogunServiceBaseUrl, basePathInterceptorRule, service, event);
        HttpGet getRequest = new HttpGet(new URI(urlString));
        byte[] resultBytes = performRequest(getRequest);
        if (resultBytes != null) {
            return objectMapper.readValue(resultBytes, new TypeReference<>() {});
        } else {
            throw new IOException("Could not read matching interceptor rules from backend");
        }
    }

    /**
     * Remove all interceptor rules for given endpoint
     * @param endpoint The endpoint
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean removeAllRulesForEndpoint(String endpoint) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint);
        HttpDelete httpDelete = new HttpDelete(new URI(urlString));
        byte[] resultBytes = performRequest(httpDelete);
        if (resultBytes != null && resultBytes.length == 1 && resultBytes[0] == 0) {
            return true;
        } else {
            throw new IOException(String.format("Could not set modified rule for all WMS actions of endpoint %s", endpoint));
        }
    }

    /**
     * Add interceptor <i>request</i> rule for given service and endpoint
     * @param endpoint The endpoint to add the rule for
     * @param service The {@link de.terrestris.shogun.interceptor.enumeration.OgcEnum.ServiceType}
     * @param rule The {@link RuleType}
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean addRequestRuleForServiceAndEndpoint(String endpoint, OgcEnum.ServiceType service, RuleType rule) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/request/%s/%s", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint, service, rule);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 1 && resultBytes[0] == 1) {
            return true;
        } else {
            throw new IOException(String.format("Could not add request rule for endpoint %s", endpoint));
        }
    }

    /**
     * Add interceptor <i>response</i> rule for given service and endpoint
     * @param endpoint The endpoint to add the rule for
     * @param service The {@link de.terrestris.shogun.interceptor.enumeration.OgcEnum.ServiceType}
     * @param rule The {@link RuleType}
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean addResponseRuleForServiceAndEndpoint(String endpoint, OgcEnum.ServiceType service, RuleType rule) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/response/%s/%s", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint, service, rule);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 1 && resultBytes[0] == 1) {
            return true;
        } else {
            throw new IOException(String.format("Could not add response rule for endpoint %s", endpoint));
        }
    }

    /**
     * Add <b>both</b> interceptor rules (<i>request</i> and <i>response</i>) for given service and endpoint
     * @param endpoint The endpoint to add the rule for
     * @param service The {@link de.terrestris.shogun.interceptor.enumeration.OgcEnum.ServiceType}
     * @param rule The {@link RuleType}
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean addRuleForEndpoint(String endpoint, OgcEnum.ServiceType service, RuleType rule) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/all/%s/%s", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint, service, rule);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 1 && resultBytes[0] == 1) {
            return true;
        } else {
            throw new IOException(String.format("Could not add request and response rule for endpoint %s", endpoint));
        }
    }

    /**
     * Set modified rule for all WMS requests and responses for given endpoint
     * @param endpoint The endpoint
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean setModifyForAllWmsActions(String endpoint) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/modifyAllWms", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 0) {
            return true;
        } else {
            throw new IOException(String.format("Could not set modified rule for all WMS actions of endpoint %s", endpoint));
        }
    }

    /**
     * Set modified rule for all WFS requests and responses for given endpoint
     * @param endpoint The endpoint
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean setModifyForAllWfsActions(String endpoint) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/modifyAllWfs", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 0) {
            return true;
        } else {
            throw new IOException(String.format("Could not set modified rule for all WFS actions of endpoint %s", endpoint));
        }
    }

    /**
     * Set modified rule for all WMS requests for given endpoint
     * @param endpoint The endpoint
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean setModifyForAllWmsRequests(String endpoint) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/modifyAllWmsRequests", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 0) {
            return true;
        } else {
            throw new IOException(String.format("Could not set modified rule for all WMS requests of endpoint %s", endpoint));
        }
    }

    /**
     * Set modified rule for all WFS requests for given endpoint
     * @param endpoint The endpoint
     * @return true if successful, false otherwise
     * @throws Exception The exception
     */
    public boolean setModifyForAllWfsRequests(String endpoint) throws Exception {
        String urlString = String.format("%s%s/endpoint/%s/modifyAllWfsRequests", this.shogunServiceBaseUrl, basePathInterceptorRule, endpoint);
        HttpPost httpPost = new HttpPost(new URI(urlString));
        byte[] resultBytes = performRequest(httpPost);
        if (resultBytes != null && resultBytes.length == 0) {
            return true;
        } else {
            throw new IOException(String.format("Could not set modified rule for all WFS requests of endpoint %s", endpoint));
        }
    }

}
