package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.ImageFile;
import de.terrestris.shoguncore.service.ImageFileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imagefiles")
@ConditionalOnExpression("${controller.imagefiles.enabled:true}")
public class ImageFileController extends BaseController<ImageFileService, ImageFile> { }
