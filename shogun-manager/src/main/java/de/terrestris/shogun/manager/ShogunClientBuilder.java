/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.manager;

import lombok.extern.log4j.Log4j2;

/**
 * SHOGun manager client builder
 */
@Log4j2
public class ShogunClientBuilder {

    private String serviceBaseUrl;
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
        this.serviceBaseUrl = shogunServiceBaseUrl;
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

        if (serviceBaseUrl == null) {
            throw new IllegalStateException("Service base URL required");
        }

        if (managerType == null) {
            throw new IllegalStateException("SHOGun manager type required");
        }

        switch (managerType) {
            case GEOSERVER_INTERCEPTOR:
                return new ShogunGsInterceptorManager(adminUser, adminPassword, serviceBaseUrl);
            case GWC:
                return new ShogunGwcManager(adminUser, adminPassword, serviceBaseUrl);
            default:
                log.warn("Manager type unknown…");
                return null;
        }
    }
}
