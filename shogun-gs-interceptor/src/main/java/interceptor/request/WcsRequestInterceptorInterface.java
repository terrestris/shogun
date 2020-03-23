package de.terrestris.shogun.interceptor.request;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface WcsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeCoverage(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetCoverage(MutableHttpServletRequest request);

}
