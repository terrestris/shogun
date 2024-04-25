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
package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.security.permission.PublicInstancePermission;
import de.terrestris.shogun.lib.repository.security.permission.PublicInstancePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PublicInstancePermissionService {

    @Autowired
    private PublicInstancePermissionRepository publicInstancePermissionRepository;

    @PreAuthorize("hasRole('ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setPublic(BaseEntity entity, boolean isPublic) {
        if (isPublic) {
            Optional<PublicInstancePermission> publicOpt = publicInstancePermissionRepository.findByEntityId(entity.getId());
            if (publicOpt.isPresent()) {
                return;
            }
            PublicInstancePermission publicInstancePermission = new PublicInstancePermission();
            publicInstancePermission.setEntityId(entity.getId());
            publicInstancePermissionRepository.save(publicInstancePermission);
        } else {
            publicInstancePermissionRepository.deleteByEntityId(entity.getId());
        }
    }

    public boolean getPublic(BaseEntity entity) {
        return publicInstancePermissionRepository.findByEntityId(entity.getId()).isPresent();
    }
}
