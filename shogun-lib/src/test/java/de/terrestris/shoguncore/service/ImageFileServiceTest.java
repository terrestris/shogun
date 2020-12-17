package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.ImageFile;
import org.junit.Before;
import org.mockito.InjectMocks;

public class ImageFileServiceTest extends BaseServiceTest<ImageFileService, ImageFile> {

    @InjectMocks
    ImageFileService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(ImageFile.class);
    }

}
