package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.ImageFile;
import de.terrestris.shoguncore.service.ImageFileService;
import org.springframework.stereotype.Component;

@Component
public class ImageFileGraphQLProvider extends BaseGraphQLDataFetcher<ImageFile, ImageFileService> { }
