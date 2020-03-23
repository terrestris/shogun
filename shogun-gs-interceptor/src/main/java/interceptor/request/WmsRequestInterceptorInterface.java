package de.terrestris.shogun.interceptor.request;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface WmsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetMap(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeLayer(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetLegendGraphic(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetStyles(MutableHttpServletRequest request);

}
