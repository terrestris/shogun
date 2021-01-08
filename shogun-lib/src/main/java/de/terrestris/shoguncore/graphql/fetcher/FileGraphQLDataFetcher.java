package de.terrestris.shoguncore.graphql.fetcher;

import de.terrestris.shoguncore.model.File;
import de.terrestris.shoguncore.service.FileService;
import org.springframework.stereotype.Component;

@Component
public class FileGraphQLDataFetcher extends BaseGraphQLDataFetcher<File, FileService> { }
