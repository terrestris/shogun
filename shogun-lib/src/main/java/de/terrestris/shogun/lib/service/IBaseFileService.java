package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.repository.BaseFileRepository;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface IBaseFileService<T extends BaseFileRepository<S, Long> & JpaSpecificationExecutor<S>, S extends File> {

    Optional<S> findOne(UUID fileUuid);

    S create(MultipartFile uploadFile) throws Exception;

    void isValidType(String contentType) throws InvalidContentTypeException;
}
