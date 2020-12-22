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
    @GraphQLQuery(name = "applicationById")
    public DataFetcher getApplicationById() {
        return dataFetchingEnvironment -> {
            Long applicationId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.applicationService.findOne(applicationId);
        };
    }
    @GraphQLQuery(name = "allApplications")
    public DataFetcher getAllApplications() {
        return dataFetchingEnvironment -> this.applicationService.findAll();
    }

    // Layer
    @GraphQLQuery(name = "layerById")
    public DataFetcher getLayerById() {
        return dataFetchingEnvironment -> {
            Long layerId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.layerService.findOne(layerId);
        };
    }
    @GraphQLQuery(name = "allLayers")
    public DataFetcher getAllLayers() {
        return dataFetchingEnvironment -> this.layerService.findAll();
    }

    // User
    @GraphQLQuery(name = "userById")
    public DataFetcher getUserById() {
        return dataFetchingEnvironment -> {
            Long userId = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.userService.findOne(userId);
        };
    }
    @GraphQLQuery(name = "allUsers")
    public DataFetcher getAllUsers() {
        return dataFetchingEnvironment -> this.userService.findAll();
    }

    // File
    @GraphQLQuery(name = "fileByUuid")
    public DataFetcher getFileByUuid() {
        return dataFetchingEnvironment -> {
            UUID uuid = dataFetchingEnvironment.getArgument("uuid");
            return this.fileService.findOne(uuid);
        };
    }
    @GraphQLQuery(name = "fileById")
    public DataFetcher getFileById() {
        return dataFetchingEnvironment -> {
            Long id = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.fileService.findOne(id);
        };
    }
    @GraphQLQuery(name = "allFiles")
    public DataFetcher getAllFiles() {
        return dataFetchingEnvironment -> this.fileService.findAll();
    }

    // ImageFile
    @GraphQLQuery(name = "imageFileByUuid")
    public DataFetcher getImageFileByUuid() {
        return dataFetchingEnvironment -> {
            UUID uuid = dataFetchingEnvironment.getArgument("uuid");
            return this.imageFileService.findOne(uuid);
        };
    }
    @GraphQLQuery(name = "imageFileById")
    public DataFetcher getImageFileById() {
        return dataFetchingEnvironment -> {
            Long id = ((Integer) dataFetchingEnvironment.getArgument("id")).longValue();
            return this.imageFileService.findOne(id);
        };
    }
    @GraphQLQuery(name = "allImageFiles")
    public DataFetcher getAllImageFiles() {
        return dataFetchingEnvironment -> this.imageFileService.findAll();
    }

}
