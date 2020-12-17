package de.terrestris.shoguncore.service;

import org.junit.Before;

public interface IBaseServiceTest {
    /**
     * All service tests must call the init method (including the {@link Before})
     * to set the desired service.
     */
    void init();
}
