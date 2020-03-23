package de.terrestris.shogun.interceptor.response;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shogun.lib.dto.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public interface WpsResponseInterceptorInterface {

    HttpResponse interceptGetCapabilities(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptDescribeProcess(MutableHttpServletRequest request, HttpResponse response);

    HttpResponse interceptExecute(MutableHttpServletRequest request, HttpResponse response);

}
