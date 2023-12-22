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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Cache;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CacheService {

    @PersistenceContext
    private EntityManager entityManager;

    public void evictCache() throws Exception {

        if (entityManager == null) {
           throw new Exception("Could not get the entity manager.");
        }

        entityManager.clear();

        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Cache cache = sessionFactory.getCache();

        cache.evictAllRegions();
    }

    public void evictCacheRegions(String... region) throws Exception {
        if (region == null) {
            return;
        }
        if (entityManager == null) {
            throw new Exception("Could not get the entity manager.");
        }
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Cache cache = sessionFactory.getCache();
        for (String r : region) {
            if (StringUtils.isEmpty(r)) {
                continue;
            }
            try {
                cache.evictRegion(r);
            } catch (NullPointerException e) {
                log.error("Could not find cache region {}. Region was not cleared.", r);
                log.trace("Full stack trace", e);
            }
        }
    }
}
