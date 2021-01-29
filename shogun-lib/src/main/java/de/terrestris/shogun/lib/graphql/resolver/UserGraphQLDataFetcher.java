package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLDataFetcher extends BaseGraphQLDataFetcher<User, UserService> { }
