package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.service.ImageFileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imagefiles")
public class ImageFileController extends BaseController<ImageFileService, ImageFile> {
}
