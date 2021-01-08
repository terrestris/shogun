package de.terrestris.shoguncore.graphql.resolver;

import de.terrestris.shoguncore.model.File;
import de.terrestris.shoguncore.service.FileService;
import org.springframework.stereotype.Component;

@Component
public class FileGraphQLDataFetcher extends BaseGraphQLDataFetcher<File, FileService> {
    // TODO Override create and update to do nothing?
}
