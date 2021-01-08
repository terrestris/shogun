package de.terrestris.shoguncore.graphql.fetchers.security;

import de.terrestris.shoguncore.graphql.fetchers.BaseGraphQLDataFetcher;
import de.terrestris.shoguncore.model.security.Identity;
import de.terrestris.shoguncore.service.security.IdentityService;
import org.springframework.stereotype.Component;

@Component
public class IdentityGraphQLProvider extends BaseGraphQLDataFetcher<Identity, IdentityService> { }
