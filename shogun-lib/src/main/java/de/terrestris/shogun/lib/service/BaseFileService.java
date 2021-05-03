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
package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.repository.BaseFileRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public abstract class BaseFileService<T extends BaseFileRepository<S, Long> & JpaSpecificationExecutor<S>, S extends File> extends BaseService<T, S> implements IBaseFileService<T, S> {

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    public Optional<S> findOne(UUID fileUuid) { return repository.findByFileUuid(fileUuid);
    }

    public abstract S create(MultipartFile uploadFile, Boolean writeToSystem) throws Exception;

}
