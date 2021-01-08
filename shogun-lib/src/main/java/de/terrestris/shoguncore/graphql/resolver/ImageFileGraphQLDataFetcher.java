package de.terrestris.shoguncore.graphql.resolver;

import de.terrestris.shoguncore.model.ImageFile;
import de.terrestris.shoguncore.service.ImageFileService;
import org.springframework.stereotype.Component;

@Component
public class ImageFileGraphQLDataFetcher extends BaseGraphQLDataFetcher<ImageFile, ImageFileService> { }
