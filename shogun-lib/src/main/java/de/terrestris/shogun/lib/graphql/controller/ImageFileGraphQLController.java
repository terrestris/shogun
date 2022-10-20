package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.service.ImageFileService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ImageFileGraphQLController extends BaseGraphQLController<ImageFile, ImageFileService> {

    @QueryMapping
    public List<ImageFile> allImageFiles() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<ImageFile> imageFileById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public List<ImageFile> allImageFilesByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

}
