package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.Group;
import org.junit.Before;
import org.mockito.InjectMocks;

public class GroupServiceTest extends BaseServiceTest<GroupService, Group> {

    @InjectMocks
    GroupService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(Group.class);
    }

}
