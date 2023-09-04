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

import com.jayway.jsonpath.Filter;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionEvaluator extends BaseEntityPermissionEvaluator<User> {

    @Autowired
    UserProviderService userProviderService;

    @Override
    public Page<User> findAll(User user, Pageable pageable, Filter filter, BaseCrudRepository<User, Long> repository,
                              Class<User> baseEntityClass) {
        Page<User> users = super.findAll(user, pageable, filter, repository, baseEntityClass);

        users.forEach(u -> userProviderService.setTransientRepresentations(u));

        return users;
    }
}
