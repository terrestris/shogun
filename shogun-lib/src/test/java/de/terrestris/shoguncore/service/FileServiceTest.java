package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.File;
import org.junit.Before;
import org.mockito.InjectMocks;

public class FileServiceTest extends BaseServiceTest<FileService, File> {

    @InjectMocks
    FileService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(File.class);
    }

}
