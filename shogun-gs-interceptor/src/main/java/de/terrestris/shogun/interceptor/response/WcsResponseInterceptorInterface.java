package de.terrestris.shogun.interceptor.response;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shoguncore.dto.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public interface WcsResponseInterceptorInterface {

    HttpResponse interceptGetCapabilities(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptDescribeCoverage(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetCoverage(MutableHttpServletRequest request, HttpResponse response);

}
