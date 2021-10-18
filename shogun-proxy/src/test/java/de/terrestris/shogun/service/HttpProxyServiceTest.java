package de.terrestris.shogun.service;

import de.terrestris.shogun.config.HttpProxyConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = HttpProxyConfig.class)
@ActiveProfiles("proxy-test")
public class HttpProxyServiceTest {

    @Autowired
    protected HttpProxyService httpProxyService;

    @Test
    @DisplayName("Return status code 400 if request and base URL are null")
    public void proxy_returns_400_when_no_url_is_given() {
        final ResponseEntity responseEntity = httpProxyService.doProxy(null, null, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

    @Test
    @DisplayName("Return status code 400 if base URL is empty")
    public void proxy_returns_400_when_baseUrl_is_empty() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String baseUrl = StringUtils.EMPTY;
        final ResponseEntity responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

//    @Test
//    public void proxy_returns_500_when_baseUrl_is_no_valid_URL() {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        final String baseUrl = "$$$$___S04___$$$$";
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_500, responseEntity.getBody());
//    }
//
//    @Test
//    public void proxy_returns_502_when_baseUrl_is_not_in_URL_whitelist() {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        final String baseUrl = "https://unallowedHost.com/unallowedPath";
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_GATEWAY, responseEntity.getStatusCode());
//        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_502, responseEntity.getBody());
//    }
//
//    @Test
//    public void proxy_returns_405_for_unsupported_HTTP_methods() {
//        // currently unsupported
//        final HttpMethod[] unsupportedHttpMethods = new HttpMethod[]{
//            HttpMethod.DELETE,
//            HttpMethod.PUT,
//            HttpMethod.HEAD,
//            HttpMethod.PATCH,
//            HttpMethod.TRACE,
//            HttpMethod.OPTIONS
//        };
//        final String baseUrl = "https://www.terrestris.de/internet.txt";
//
//        PowerMockito.mockStatic(HttpUtil.class);
//        for (HttpMethod unsupportedHttpMethod : unsupportedHttpMethods) {
//            HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//            Mockito.when(mockedRequest.getMethod()).thenReturn(unsupportedHttpMethod.name());
//            final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//            Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
//            Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_405, responseEntity.getBody());
//        }
//    }
//
//    @Test
//    public void proxy_returns_200_for_allowed_GET_request() throws URISyntaxException, HttpException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/internet.txt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenReturn(mockedRespone);
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());
//
//        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
//        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
//    }
//
//    @Test
//    public void proxy_returns_400_for_erroneous_GET_requests() throws URISyntaxException, HttpException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/internet.txt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//        final String msg = "ERROR";
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
//    }
//
//    @Test
//    public void proxy_returns_200_for_allowed_POST_request() throws URISyntaxException, HttpException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
//        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenReturn(mockedRespone);
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());
//
//        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
//        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
//    }
//
//    @Test
//    public void proxy_returns_400_for_erroneous_POST_requests() throws URISyntaxException, HttpException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/internet.txt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//        final String msg = "ERROR";
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
//        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
//    }
//
//
//    @Test
//    public void proxy_returns_200_for_allowed_FormMultipartPost_request() throws URISyntaxException, HttpException, IOException, ServletException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
//        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenReturn(mockedRespone);
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());
//
//        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
//        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
//    }
//
//    @Test
//    public void proxy_returns_400_for_erroneous_FormMultipartPost_requests() throws URISyntaxException, HttpException, IOException, ServletException {
//        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
//        PowerMockito.mockStatic(HttpUtil.class);
//        final String internetContent = "THE INTERNET!";
//        final String baseUrl = "https://www.terrestris.de/internet.txt";
//        final URI baseUri = new URI(baseUrl);
//        Response mockedRespone = Mockito.mock(Response.class);
//        final String msg = "ERROR";
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//        HttpStatus status = HttpStatus.OK;
//
//        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
//        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
//        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
//        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
//        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
//        Mockito.when(HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));
//
//        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
//        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
//    }
}
