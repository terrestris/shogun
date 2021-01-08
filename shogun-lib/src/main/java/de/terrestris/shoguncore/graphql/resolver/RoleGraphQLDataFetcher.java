package de.terrestris.shoguncore.graphql.resolver;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.service.RoleService;
import org.springframework.stereotype.Component;

@Component
public class RoleGraphQLDataFetcher extends BaseGraphQLDataFetcher<Role, RoleService> { }
