package de.terrestris.shogunboot.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    protected final Logger LOG = LogManager.getLogger(getClass());

    public void run(ApplicationArguments args) {
        LOG.info("Initializing the application");
    }
}
