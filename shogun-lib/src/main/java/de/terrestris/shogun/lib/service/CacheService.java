package de.terrestris.shogun.lib.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import org.hibernate.Cache;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
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
}
