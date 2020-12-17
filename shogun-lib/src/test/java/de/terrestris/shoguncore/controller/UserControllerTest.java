package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    User.class
})
public class UserControllerTest extends BaseControllerTest<UserController, UserRepository, User> {

    @Before
    public void setBasePath() {
        basePath = "/users";
    }

    @Before
    public void setEntityClass() {
        entityClass = User.class;
    }

    @Before
    public void insertTestData() {
        User entity1 = new User();
        User entity2 = new User();
        User entity3 = new User();

        entity1.setUsername("User 1");
        entity1.setEmail("user1@shogun.de");
        entity2.setUsername("User 2");
        entity2.setEmail("user2@shogun.de");
        entity3.setUsername("User 3");
        entity3.setEmail("user3@shogun.de");

        ArrayList<User> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<User> persistedEntities = (List<User>) repository.saveAll(entities);

        testData = persistedEntities;
    }

    @After
    public void cleanupTestData() {
        repository.deleteAll(testData);
    }

}
