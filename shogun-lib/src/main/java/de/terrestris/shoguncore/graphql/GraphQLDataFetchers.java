package de.terrestris.shoguncore.graphql;

import de.terrestris.shoguncore.repository.ApplicationRepository;
import de.terrestris.shoguncore.repository.LayerRepository;
import de.terrestris.shoguncore.repository.UserRepository;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            return this.applicationRepository.findById(applicationId);
        };
    }

    public DataFetcher getAllApplications() {
        return dataFetchingEnvironment -> this.applicationRepository.findAll();
    }

    public DataFetcher getLayerById() {
        return dataFetchingEnvironment -> {
            Long layerId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return this.layerRepository.findById(layerId);
        };
    }

    public DataFetcher getAllLayers() {
        return dataFetchingEnvironment -> this.layerRepository.findAll();
    }

    public DataFetcher getUserById() {
        return dataFetchingEnvironment -> {
            Long userId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return this.userRepository.findById(userId);
        };
    }

    public DataFetcher getAllUsers() {
        return dataFetchingEnvironment -> this.userRepository.findAll();
    }
}
