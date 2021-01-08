package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.service.ApplicationService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationGraphQLProvider extends BaseGraphQLDataFetcher<Application, ApplicationService> { }
