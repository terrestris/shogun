package de.terrestris.shoguncore.graphql.resolver.security;

import de.terrestris.shoguncore.graphql.resolver.BaseGraphQLDataFetcher;
import de.terrestris.shoguncore.model.security.Identity;
import de.terrestris.shoguncore.service.security.IdentityService;
import org.springframework.stereotype.Component;

@Component
public class IdentityGraphQLProvider extends BaseGraphQLDataFetcher<Identity, IdentityService> {  }
