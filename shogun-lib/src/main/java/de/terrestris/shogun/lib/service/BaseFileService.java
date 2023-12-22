/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.repository.BaseFileRepository;
import de.terrestris.shogun.properties.UploadProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public abstract class BaseFileService<T extends BaseFileRepository<S, Long> & JpaSpecificationExecutor<S>, S extends File> extends BaseService<T, S> implements IBaseFileService<T, S> {

    @Autowired
    private UploadProperties uploadProperties;

    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject.orElse(null), 'READ')")
    public Optional<S> findOne(UUID fileUuid) {
        return repository.findByFileUuid(fileUuid);
    }

    public abstract S create(MultipartFile uploadFile, Boolean writeToSystem) throws Exception;

    public void isValid(MultipartFile file) throws Exception {
        if (file == null) {
            throw new Exception("Given file is null.");
        } else if (file.isEmpty()) {
            throw new Exception("Given file is empty.");
        }
        this.isValidFileName(file.getOriginalFilename());
        this.isValidType(file.getContentType());
        this.verifyContentType(file);
    }

    public void verifyContentType(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String name = file.getName();
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, name);

        Tika tika = new Tika();
        String detectedMediaType = tika.detect(TikaInputStream.get(file.getBytes()), metadata);

        if (!StringUtils.equalsIgnoreCase(detectedMediaType, contentType)) {
            throw new IOException("Media type validation failed. Passed content type is " + contentType + " but detected media type is " + detectedMediaType);
        }
    }

    public void isValidType(String contentType) throws InvalidContentTypeException {
        List<String> supportedContentTypes = getSupportedContentTypes();
        boolean isMatch = PatternMatchUtils.simpleMatch(supportedContentTypes.toArray(new String[0]), contentType);
        if (!isMatch) {
            log.warn("Unsupported content type {} for upload", contentType);
            throw new InvalidContentTypeException("Unsupported content type for upload!");
        }
    }

    public void isValidFileName(String fileName) throws InvalidFileNameException {
        List<String> illegalCharacters = Arrays.asList("\\", "/", ":", "*", "?", "\"", "<", ">", "|", "\\0", "\\n");
        if (illegalCharacters.stream().anyMatch(fileName::contains)) {
            throw new InvalidFileNameException(fileName, "Filename contains illegal characters. [\\, /, :, *, ?, \", <, >, |, \\0, \\n]");
        }
    }

    /**
     * Get the file data as bytearray. Depends on storage strategy (DB vs. disk);
     *
     * @param file
     * @return
     * @throws IOException
     */
    public byte[] getFileData(S file) throws IOException {
        if (file.getPath() == null) {
            log.trace("… load file from database");
            return file.getFile();
        }
        java.io.File dataFile = new java.io.File(uploadProperties.getBasePath() + "/" + file.getPath());
        if (dataFile.exists()) {
            log.trace("… load file from disk");
            return FileUtils.readFileToByteArray(dataFile);
        } else {
            log.error("Could not load File {} from disk", file.getId());
            throw new FileNotFoundException("Could not load File " + file.getId() + " from disk");
        }
    }

}
