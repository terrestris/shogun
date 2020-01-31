package de.terrestris.shoguncore.graphql;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GraphQLDataFetchers {

    @Autowired
    UserRepository userRepository;

    public DataFetcher getUserById() {
        return dataFetchingEnvironment -> {
            Long userId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            Optional<User> user = this.userRepository.findById(userId);
            return user;
        };
    }

    public DataFetcher getAllUsers() {
        return dataFetchingEnvironment -> {
            Iterable<User> users = this.userRepository.findAll();
            return users;
        };
    }
}
