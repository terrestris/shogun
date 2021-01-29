package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.FileService;
import org.springframework.stereotype.Component;

@Component
public class FileGraphQLDataFetcher extends BaseGraphQLDataFetcher<File, FileService> {
    // TODO Override create and update to do nothing?
}
