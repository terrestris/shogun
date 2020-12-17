package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.User;
import org.junit.Before;
import org.mockito.InjectMocks;

public class UserServiceTest extends BaseServiceTest<UserService, User> {

    @InjectMocks
    UserService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(User.class);
    }

}
