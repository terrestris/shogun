package de.terrestris.shogun.interceptor.request;

import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface WpsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeProcess(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptExecute(MutableHttpServletRequest request);

}
