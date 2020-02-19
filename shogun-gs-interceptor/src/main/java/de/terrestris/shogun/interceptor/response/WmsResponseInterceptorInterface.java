package de.terrestris.shogun.interceptor.response;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shogun.lib.dto.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public interface WmsResponseInterceptorInterface {

    HttpResponse interceptGetMap(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetCapabilities(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetFeatureInfo(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptDescribeLayer(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetLegendGraphic(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptGetStyles(MutableHttpServletRequest request, HttpResponse response);

}
