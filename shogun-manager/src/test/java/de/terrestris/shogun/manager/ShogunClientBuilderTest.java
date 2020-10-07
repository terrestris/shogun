package de.terrestris.shogun.manager;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ShogunClientBuilderTest {

    @Test
    public void builder() {
        assertNotNull(ShogunClientBuilder.builder());
    }

    @Test
    public void shogunServiceBaseUrl() {
        String url = "https://test/url";
        assertNotNull(ShogunClientBuilder.builder().shogunServiceBaseUrl(url));
    }

    @Test
    public void username() {
        String userName = "Peter";
        assertNotNull(ShogunClientBuilder.builder().username(userName));
    }

    @Test
    public void password() {
        String password = "password";
        assertNotNull(ShogunClientBuilder.builder().username(password));
    }

    @Test
    public void managerType() {
        ShogunManagerType type = ShogunManagerType.GEOSERVER_INTERCEPTOR;
        assertNotNull(ShogunClientBuilder.builder().managerType(type));
    }

    @Test
    public void build() {
        assertNotNull(ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .shogunServiceBaseUrl("https://shogun")
            .managerType(ShogunManagerType.GEOSERVER_INTERCEPTOR)
            .build());
    }

    @Test
    public void build_unknownType() {
        assertNull(ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .shogunServiceBaseUrl("https://shogun")
            .managerType(ShogunManagerType.APP)
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void build_TypeMissing() {
        ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .shogunServiceBaseUrl("https://shogun")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void build_baseUrlRequired() {
        ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void build_usernamePasswordRequired() {
        ShogunClientBuilder.builder().build();
    }
}
