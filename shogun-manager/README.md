This module contains a Java client for SHOGun REST interfaces.

For example, a manager client for GeoServer interceptor can be created in the following way:
```java
String username = "interceptoradmin";
String password = "<<YOUR_ADMIN_PASSWORD>>";
String baseUrl = "https://url-to-shogun/shogun-gs-interceptor";

try (
    ShogunGsInterceptorManager manager = (ShogunGsInterceptorManager) ShogunClientBuilder.builder()
        .password(password)
        .username(username)
        .shogunServiceBaseUrl(baseUrl)
        .managerType(ShogunManagerType.GEOSERVER_INTERCEPTOR)
        .build()
    ) {

    // fetch all interceptor rules
    List<InterceptorRule> rules = manager.getAllInterceptorRules();

    // add request rule
    String endpoint = "TOP";
    boolean success = manager.addRequestRuleForServiceAndEndpoint(endpoint, OgcEnum.ServiceType.WFS, RuleType.DENY);
}  catch (Exception e) {
  System.err.println(e);
}
```