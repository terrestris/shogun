package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.repository.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    Role.class
})
public class RoleControllerTest extends BaseControllerTest<RoleController, RoleRepository, Role> {

    @Before
    public void setBasePath() {
        basePath = "/roles";
    }

    @Before
    public void setEntityClass() {
        entityClass = Role.class;
    }

    @Before
    public void insertTestData() {
        Role entity1 = new Role();
        Role entity2 = new Role();
        Role entity3 = new Role();

        entity1.setName("Role 1");
        entity2.setName("Role 2");
        entity3.setName("Role 3");

        ArrayList<Role> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<Role> persistedEntities = (List<Role>) repository.saveAll(entities);

        testData = persistedEntities;
    }

    @After
    public void cleanupTestData() {
        repository.deleteAll(testData);
    }

}
