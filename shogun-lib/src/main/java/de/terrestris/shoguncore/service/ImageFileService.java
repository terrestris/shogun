package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.ImageFile;
import de.terrestris.shoguncore.repository.ImageFileRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageFileService extends BaseService<ImageFileRepository, ImageFile> { }
