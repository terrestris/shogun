package de.terrestris.shoguncore.graphql;

import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.model.Layer;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.ApplicationRepository;
import de.terrestris.shoguncore.repository.LayerRepository;
import de.terrestris.shoguncore.repository.UserRepository;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GraphQLDataFetchers {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    LayerRepository layerRepository;

    @Autowired
    UserRepository userRepository;

    public DataFetcher getApplicationById() {
        return dataFetchingEnvironment -> {
            Long applicationId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            Optional<Application> application = this.applicationRepository.findById(applicationId);
            return application;
        };
    }

    public DataFetcher getAllApplications() {
        return dataFetchingEnvironment -> {
            Iterable<Application> applications = this.applicationRepository.findAll();
            return applications;
        };
    }

    public DataFetcher getLayerById() {
        return dataFetchingEnvironment -> {
            Long layerId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            Optional<Layer> layer = this.layerRepository.findById(layerId);
            return layer;
        };
    }

    public DataFetcher getAllLayers() {
        return dataFetchingEnvironment -> {
            Iterable<Layer> layers = this.layerRepository.findAll();
            return layers;
        };
    }

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
