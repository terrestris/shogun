/*
 * SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.EntityIdCheckRepository;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class EntityIdCheckService<T extends BaseEntity>{

    @Autowired
    protected EntityIdCheckRepository<T> repository;

    @Autowired
    private UserProviderService userProviderService;

    /**
     * Returns an array of entity ID's starting with provided search id part.
     * Considers the role of currently logged-in user while querying.
     *
     * @param id ID part used for query
     * @return Array of entity ID's limited to max 10 hits
     */
    @PreAuthorize("isAuthenticated()")
    public List<Long> idStartsWith(Long id) {
        Optional<User> userCandidate = userProviderService.getUserBySession();
        if (userCandidate.isEmpty()) {
            return List.of();
        }
        User user = userCandidate.get();


        Pageable paging = PageRequest.of(0, 10);
        return repository.idStartsWith(id, paging);
    }
}
