package de.terrestris.shogun.lib.specification;

import de.terrestris.shogun.lib.model.File;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class FileSpecification {

    public static Specification<File> findByUuid(UUID fileUuid) {
        return (root, query, cb) -> {
            return cb.equal(root.get("fileUuid"), fileUuid);
        };
    }
}
