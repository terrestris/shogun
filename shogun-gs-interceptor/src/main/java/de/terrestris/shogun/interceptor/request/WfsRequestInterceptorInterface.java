package de.terrestris.shogun.interceptor.request;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface WfsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeFeatureType(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetFeature(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptLockFeature(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptTransaction(MutableHttpServletRequest request);

}
