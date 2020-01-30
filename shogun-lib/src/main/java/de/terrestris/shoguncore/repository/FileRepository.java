package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.File;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends BaseCrudRepository<File, Long>, JpaSpecificationExecutor<File> {
}
