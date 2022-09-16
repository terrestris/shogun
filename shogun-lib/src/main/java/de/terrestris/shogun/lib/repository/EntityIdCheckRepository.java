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

package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.BaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EntityIdCheckRepository<T extends BaseEntity> {

    @Query("SELECT p.id FROM #{#entityName} p WHERE CAST( p.id as string ) LIKE CONCAT(:id,'%')")
    List<Long> idStartsWith(Long id, Pageable pageable);

    @Query("SELECT p.id FROM #{#entityName} p") // todo: test this
    List<Long> listAllIds(Pageable pageable);

}

