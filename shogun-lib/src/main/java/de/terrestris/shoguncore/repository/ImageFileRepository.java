package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.ImageFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageFileRepository extends BaseCrudRepository<ImageFile, Long>, JpaSpecificationExecutor<ImageFile> {
}
