package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.FileService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class FileGraphQLController extends BaseGraphQLController<File, FileService> {

    @QueryMapping
    public List<File> allFiles() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<File> fileById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public List<File> allFilesByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

}
