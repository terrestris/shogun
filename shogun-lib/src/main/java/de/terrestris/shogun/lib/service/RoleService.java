/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.repository.RoleRepository;
import de.terrestris.shogun.lib.service.security.permission.RoleClassPermissionService;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class RoleService extends BaseService<RoleRepository, Role> {

    @Autowired
    RoleProviderService roleProviderService;

    @Autowired
    RoleClassPermissionService roleClassPermissionService;

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Role> findAll() {
        List<Role> roles = repository.findAll();

        for (Role role : roles) {
            roleProviderService.setTransientRepresentations(role);
        }

        return roles;
    }

    @PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<Role> findAllBy(Specification specification) {
        List<Role> roles = (List<Role>) repository.findAll(specification);

        for (Role role : roles) {
            roleProviderService.setTransientRepresentations(role);
        }

        return roles;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    @Override
    public Optional<Role> findOne(Long id) {
        Optional<Role> role = repository.findById(id);

        if (role.isPresent()) {
            roleProviderService.setTransientRepresentations(role.get());
        }

        return role;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    @Transactional(readOnly = true)
    public Optional<Role> findByKeyCloakId(String keycloakId) {
        Optional<Role> role = repository.findByAuthProviderId(keycloakId);

        if (role.isPresent()) {
            roleProviderService.setTransientRepresentations(role.get());
        }

        return role;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#role, 'DELETE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Role role) {
        roleClassPermissionService.deleteAllFor(role);
        roleInstancePermissionService.deleteAllFor(role);

        repository.delete(role);
    }

}
