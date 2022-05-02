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
package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SecurityContextUtil {

    @Autowired
    protected UserRepository userRepository;

    /**
     *
     * @return
     */
    public List<GrantedAuthority> getGrantedAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ArrayList<>(authentication.getAuthorities());
    }

    /**
     * Return if user (in session) is an admin of SHOGun-GeoServer-Interceptor microservice
     * @return true if so, false otherwise
     */
    public boolean isInterceptorAdmin() {
        List<GrantedAuthority> authorities = getGrantedAuthorities();
        return authorities.stream().anyMatch(
            grantedAuthority -> StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "INTERCEPTOR_ADMIN") ||
                StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "ADMIN")
        );
    }

}
