package de.terrestris.shogun.lib.graphql;

import de.terrestris.shogun.lib.service.ApplicationService;
import de.terrestris.shogun.lib.service.LayerService;
import de.terrestris.shogun.lib.service.UserService;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQLDataFetchers {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    LayerService layerService;

    @Autowired
    UserService userService;

    public DataFetcher getApplicationById() {
        return dataFetchingEnvironment -> {
            Long applicationId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.applicationService.findOne(applicationId);
        };
    }

    public DataFetcher getAllApplications() {
        return dataFetchingEnvironment -> this.applicationService.findAll();
    }

    public DataFetcher getLayerById() {
        return dataFetchingEnvironment -> {
            Long layerId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.layerService.findOne(layerId);
        };
    }

    public DataFetcher getAllLayers() {
        return dataFetchingEnvironment -> this.layerService.findAll();
    }

    public DataFetcher getUserById() {
        return dataFetchingEnvironment -> {
            Long userId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.userService.findOne(userId);
        };
    }

    public DataFetcher getAllUsers() {
        return dataFetchingEnvironment -> this.userService.findAll();
    }
}
