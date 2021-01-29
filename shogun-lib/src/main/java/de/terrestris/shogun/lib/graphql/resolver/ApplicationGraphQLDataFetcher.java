package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.service.ApplicationService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationGraphQLDataFetcher extends BaseGraphQLDataFetcher<Application, ApplicationService> { }
