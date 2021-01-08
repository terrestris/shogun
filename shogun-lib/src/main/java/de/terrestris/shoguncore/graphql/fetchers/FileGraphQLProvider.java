package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.File;
import de.terrestris.shoguncore.service.FileService;
import org.springframework.stereotype.Component;

@Component
public class FileGraphQLProvider extends BaseGraphQLDataFetcher<File, FileService> { }
