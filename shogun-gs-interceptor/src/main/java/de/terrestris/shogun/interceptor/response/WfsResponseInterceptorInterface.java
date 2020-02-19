package de.terrestris.shogun.interceptor.response;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shogun.lib.dto.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public interface WfsResponseInterceptorInterface {

    HttpResponse interceptGetCapabilities(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptDescribeFeatureType(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetFeature(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptLockFeature(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptTransaction(MutableHttpServletRequest request, HttpResponse response);

}
