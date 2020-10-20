package de.terrestris.shogun.manager;

import lombok.extern.log4j.Log4j2;

/**
 * SHOGun manager client builder
 */
@Log4j2
public class ShogunClientBuilder {

    private String shogunServiceBaseUrl;
    private String adminUser;
    private String adminPassword;
    private ShogunManagerType managerType;

    private ShogunClientBuilder() {
    }

    /**
     * Returns a new ShogunClientBuilder builder.
     */
    public static ShogunClientBuilder builder() {
        return new ShogunClientBuilder();
    }

    /**
     * Set SHOGUN base url for builder
     * @param shogunServiceBaseUrl The base URL to SHOGun
     * @return builder
     */
    public ShogunClientBuilder shogunServiceBaseUrl(String shogunServiceBaseUrl) {
        this.shogunServiceBaseUrl = shogunServiceBaseUrl;
        return this;
    }

    /**
     * Set SHOGun admin user name
     * @param username The username of a SHOGun admin user having appropriate rules
     * @return builder
     */
    public ShogunClientBuilder username(String username) {
        this.adminUser = username;
        return this;
    }

    /**
     * Set SHOGun admin user password
     * @param password The password of a SHOGun admin user having appropriate rules
     * @return builder
     */
    public ShogunClientBuilder password(String password) {
        this.adminPassword = password;
        return this;
    }

    /**
     * Set SHOGUN manager type
     * @param managerType The {@link ShogunManagerType} to build
     * @return builder
     */
    public ShogunClientBuilder managerType(ShogunManagerType managerType) {
        this.managerType = managerType;
        return this;
    }

    /**
     * Generate manager instance based on configuration
     * @return Instance of {@link AbstractShogunManager}
     */
    public AbstractShogunManager build() {
        if (adminUser == null) {
            throw new IllegalStateException("SHOGun admin user name required");
        }

        if (adminPassword == null) {
            throw new IllegalStateException("SHOGun admin user password required");
        }

        if (shogunServiceBaseUrl == null) {
            throw new IllegalStateException("SHOGun base URL required");
        }

        if (managerType == null) {
            throw new IllegalStateException("SHOGun manager type required");
        }

        switch (managerType) {
            case GEOSERVER_INTERCEPTOR:
                return new ShogunGsInterceptorManager(adminUser, adminPassword, shogunServiceBaseUrl);
            default:
                log.warn("Manager type unknown…");
                return null;
        }
    }
}
