package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.ImageFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageFileRepository extends BaseCrudRepository<ImageFile, Long>, JpaSpecificationExecutor<ImageFile> {
}
