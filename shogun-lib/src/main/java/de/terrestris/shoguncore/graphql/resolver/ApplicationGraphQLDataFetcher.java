package de.terrestris.shoguncore.graphql.resolver;

import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.service.ApplicationService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationGraphQLDataFetcher extends BaseGraphQLDataFetcher<Application, ApplicationService> { }
