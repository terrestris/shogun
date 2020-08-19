package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.File;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends BaseCrudRepository<File, Long>, JpaSpecificationExecutor<File> {

    Optional<File> findByFileUuid(UUID uuid);

}
