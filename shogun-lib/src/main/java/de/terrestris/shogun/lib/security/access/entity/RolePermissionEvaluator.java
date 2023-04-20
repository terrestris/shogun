/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2023-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionEvaluator extends BaseEntityPermissionEvaluator<Role> {

    @Autowired
    RoleProviderService roleProviderService;

    @Override
    public Page<Role> findAll(User user, Pageable pageable, BaseCrudRepository<Role, Long> repository,
        Class<Role> baseEntityClass) {
        Page<Role> roles = super.findAll(user, pageable, repository, baseEntityClass);

        roles.forEach(u -> roleProviderService.setTransientRepresentations(u));

        return roles;
    }
}
