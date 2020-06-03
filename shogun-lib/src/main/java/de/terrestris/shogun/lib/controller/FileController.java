package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.FileService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/files")
public class FileController extends BaseFileController<FileService, File> { }
