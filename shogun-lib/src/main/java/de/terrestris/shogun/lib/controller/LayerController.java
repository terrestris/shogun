package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.service.LayerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/layers")
@ConditionalOnExpression("${controller.layers.enabled:true}")
public class LayerController extends BaseController<LayerService, Layer> { }
