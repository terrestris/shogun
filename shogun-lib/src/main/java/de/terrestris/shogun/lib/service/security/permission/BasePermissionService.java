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
package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.model.security.permission.BasePermission;
import de.terrestris.shogun.lib.repository.security.permission.BasePermissionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Log4j2
public abstract class BasePermissionService<T extends BasePermissionRepository<S, Long> & JpaSpecificationExecutor<S>, S extends BasePermission> {

    @Autowired
    protected T repository;

}
