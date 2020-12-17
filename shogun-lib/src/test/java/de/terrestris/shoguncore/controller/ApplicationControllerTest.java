package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.repository.ApplicationRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    Application.class
})
public class ApplicationControllerTest extends BaseControllerTest<ApplicationController, ApplicationRepository, Application> {

    @Before
    public void setBasePath() {
        basePath = "/applications";
    }

    @Before
    public void setEntityClass() {
        entityClass = Application.class;
    }

    @Before
    public void insertTestData() {
        Application entity1 = new Application();
        Application entity2 = new Application();
        Application entity3 = new Application();

        entity1.setName("Application 1");
        entity2.setName("Application 2");
        entity3.setName("Application 3");

        ArrayList<Application> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<Application> persistedEntities = (List<Application>) repository.saveAll(entities);

        testData = persistedEntities;
    }

    @After
    public void cleanupTestData() {
        repository.deleteAll(testData);
    }

}
