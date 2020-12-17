package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.Application;
import org.junit.Before;
import org.mockito.InjectMocks;

public class ApplicationServiceTest extends BaseServiceTest<ApplicationService, Application> {

    @InjectMocks
    ApplicationService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(Application.class);
    }

}
