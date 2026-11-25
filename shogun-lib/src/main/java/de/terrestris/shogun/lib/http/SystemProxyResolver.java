/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2026-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.http;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.HttpHost;

import java.net.*;
import java.util.List;

/**
 * Detects the HTTP proxy that should be used for a given target {@link URI}.
 *
 * <p>The following sources are evaluated (in this order):
 * <ol>
 *   <li>{@code http.nonProxyHosts} / {@code NO_PROXY} (skips the proxy)</li>
 *   <li>JVM system properties ({@code http(s).proxyHost}, {@code http(s).proxyPort})</li>
 *   <li>Environment variables ({@code http(s)_proxy}, upper case variants)</li>
 *   <li>The default {@link ProxySelector}</li>
 * </ol>
 *
 * <p>Note: {@code java.net.useSystemProxies} is intentionally not set here. If needed, configure it
 * on the JVM via {@code -Djava.net.useSystemProxies=true}.
 */
@Log4j2
final class SystemProxyResolver {

    private SystemProxyResolver() {
    }

    /**
     * @param uri The target URI of the request.
     * @return The proxy to use or {@code null} if the target should be contacted directly.
     * @throws UnknownHostException if the proxy host cannot be resolved
     */
    static HttpHost resolve(URI uri) throws UnknownHostException {
        String scheme = uri.getScheme();
        String proxyHost = null;
        int proxyPort = -1;

        String nonProxyHosts = System.getProperty("http.nonProxyHosts");
        String noProxyEnv = System.getenv("NO_PROXY");
        String targetHost = uri.getHost();

        if (targetHost != null && (StringUtils.isNotEmpty(nonProxyHosts) || StringUtils.isNotEmpty(noProxyEnv))) {
            String[] patterns;

            if (StringUtils.isNotEmpty(nonProxyHosts)) {
                patterns = nonProxyHosts.split("\\|");
            } else {
                patterns = noProxyEnv.split(",");
            }

            for (String pattern : patterns) {
                pattern = pattern.trim();
                String regex = pattern.replace(".", "\\.").replace("*", ".*");
                if (targetHost.matches(regex)) {
                    log.debug("Host {} matches nonProxyHost pattern {} — skipping proxy.", targetHost, pattern);
                    return null;
                }
            }
        }

        if (Strings.CI.equals("http", scheme)) {
            proxyHost = System.getProperty("http.proxyHost");
            String port = System.getProperty("http.proxyPort");
            if (StringUtils.isNotEmpty(port)) {
                proxyPort = Integer.parseInt(port);
            }
        } else if (Strings.CI.equals("https", scheme)) {
            proxyHost = System.getProperty("https.proxyHost");
            String port = System.getProperty("https.proxyPort");
            if (StringUtils.isNotEmpty(port)) {
                proxyPort = Integer.parseInt(port);
            }
        }

        if (StringUtils.isEmpty(proxyHost) && proxyPort <= 0) {
            String envProxy = null;
            if (Strings.CI.equals("http", scheme)) {
                envProxy = System.getenv("http_proxy");
                if (StringUtils.isEmpty(envProxy)) {
                    envProxy = System.getenv("HTTP_PROXY");
                }
            } else if (Strings.CI.equals("https", scheme)) {
                envProxy = System.getenv("https_proxy");
                if (StringUtils.isEmpty(envProxy)) {
                    envProxy = System.getenv("HTTPS_PROXY");
                }
            }

            if (Strings.CS.startsWith(envProxy, "http") && StringUtils.isNotBlank(envProxy)) {
                try {
                    URI proxyUri = URI.create(envProxy);
                    proxyHost = proxyUri.getHost();
                    proxyPort = proxyUri.getPort();
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid proxy URI in environment variable: {}", envProxy, e);
                }
            }
        }

        if (proxyHost != null && proxyPort > 0) {
            log.debug("Using proxy from system properties: Host={}, Port={}", proxyHost, proxyPort);
            return new HttpHost(InetAddress.getByName(proxyHost), proxyPort);
        }

        log.debug("Attempting to detect system proxy via ProxySelector.");
        List<Proxy> proxyList = ProxySelector.getDefault().select(uri);

        for (Proxy proxy : proxyList) {
            if (proxy.address() instanceof InetSocketAddress address) {
                log.debug("Detected a system wide proxy: Host={}, Port={}", address.getHostName(), address.getPort());

                return new HttpHost(
                    InetAddress.getByName(address.getHostName()),
                    address.getPort()
                );
            }
        }

        return null;
    }

    /**
     * @param uri The target URI of the request.
     * @return Credentials for the proxy configured via {@code http(s).proxyUser} and
     * {@code http(s).proxyPassword} or {@code null} if none are configured.
     */
    static UsernamePasswordCredentials proxyCredentials(URI uri) {
        String prefix;
        if (Strings.CI.equals(uri.getScheme(), "http")) {
            prefix = "http";
        } else if (Strings.CI.equals(uri.getScheme(), "https")) {
            prefix = "https";
        } else {
            return null;
        }

        String user = System.getProperty(prefix + ".proxyUser");
        String password = System.getProperty(prefix + ".proxyPassword");

        if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
            return null;
        }

        log.debug("Using proxy user and password for the {} proxy", prefix);
        return new UsernamePasswordCredentials(user, password.toCharArray());
    }

}
