package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.service.ImageFileService;
import org.springframework.stereotype.Component;

@Component
public class ImageFileGraphQLDataFetcher extends BaseGraphQLDataFetcher<ImageFile, ImageFileService> { }
