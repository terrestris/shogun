package de.terrestris.shogun.lib.graphql;

import de.terrestris.shogun.lib.service.*;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GraphQLDataFetchers {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    LayerService layerService;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Autowired
    ImageFileService imageFileService;

    // Application
    public DataFetcher getApplicationById() {
        return dataFetchingEnvironment -> {
            Long applicationId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.applicationService.findOne(applicationId);
        };
    }
    public DataFetcher getAllApplications() {
        return dataFetchingEnvironment -> this.applicationService.findAll();
    }

    // Layer
    public DataFetcher getLayerById() {
        return dataFetchingEnvironment -> {
            Long layerId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.layerService.findOne(layerId);
        };
    }
    public DataFetcher getAllLayers() {
        return dataFetchingEnvironment -> this.layerService.findAll();
    }

    // User
    public DataFetcher getUserById() {
        return dataFetchingEnvironment -> {
            Long userId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.userService.findOne(userId);
        };
    }
    public DataFetcher getAllUsers() {
        return dataFetchingEnvironment -> this.userService.findAll();
    }

    // File
    public DataFetcher getFileByUuid() {
        return dataFetchingEnvironment -> {
            UUID uuid = dataFetchingEnvironment.getArgument("uuid");
            return this.fileService.findOne(uuid);
        };
    }
    public DataFetcher getFileById() {
        return dataFetchingEnvironment -> {
            Long id = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.fileService.findOne(id);
        };
    }
    public DataFetcher getAllFiles() {
        return dataFetchingEnvironment -> this.fileService.findAll();
    }

    // ImageFile
    public DataFetcher getImageFileById() {
        return dataFetchingEnvironment -> {
            Long id = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.imageFileService.findOne(id);
        };
    }
    public DataFetcher getAllImageFiles() {
        return dataFetchingEnvironment -> this.imageFileService.findAll();
    }

}
