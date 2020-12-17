package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.Role;
import org.junit.Before;
import org.mockito.InjectMocks;

public class RoleServiceTest extends BaseServiceTest<RoleService, Role> {

    @InjectMocks
    RoleService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(Role.class);
    }

}
