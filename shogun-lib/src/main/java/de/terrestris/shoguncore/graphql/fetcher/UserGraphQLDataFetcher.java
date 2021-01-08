package de.terrestris.shoguncore.graphql.fetcher;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLDataFetcher extends BaseGraphQLDataFetcher<User, UserService> { }
