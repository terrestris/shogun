package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.FileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@ConditionalOnExpression("${controller.files.enabled:true}")
public class FileController extends BaseFileController<FileService, File> { }
