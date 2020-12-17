package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.Layer;
import org.junit.Before;
import org.mockito.InjectMocks;

public class LayerServiceTest extends BaseServiceTest<LayerService, Layer> {

    @InjectMocks
    LayerService service;

    @Before
    public void init() {
        super.setService(service);
        super.setEntityClass(Layer.class);
    }

}
