package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.service.RoleService;
import org.springframework.stereotype.Component;

@Component
public class RoleGraphQLProvider extends BaseGraphQLDataFetcher<Role, RoleService> { }
