package de.terrestris.shogun.lib.repository;

import de.terrestris.shogun.lib.model.File;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends BaseFileRepository<File, Long>, JpaSpecificationExecutor<File> { }
