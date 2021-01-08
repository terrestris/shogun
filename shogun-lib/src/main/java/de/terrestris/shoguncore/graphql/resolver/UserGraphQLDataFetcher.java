package de.terrestris.shoguncore.graphql.resolver;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLDataFetcher extends BaseGraphQLDataFetcher<User, UserService> { }
