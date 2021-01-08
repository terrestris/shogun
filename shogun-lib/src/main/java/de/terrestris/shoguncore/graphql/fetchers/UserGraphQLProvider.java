package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLProvider extends BaseGraphQLDataFetcher<User, UserService> { }
