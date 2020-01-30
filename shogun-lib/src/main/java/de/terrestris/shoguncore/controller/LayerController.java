package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Layer;
import de.terrestris.shoguncore.service.LayerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/layers")
public class LayerController extends BaseController<LayerService, Layer> {
}
