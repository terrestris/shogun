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
import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        this.isValidType(file.getContentType());
        this.verifyContentType(file);
    }

    public void verifyContentType(MultipartFile file) throws IOException, TikaException {
        String contentType = file.getContentType();
        String name = file.getName();
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, name);
        TikaConfig tika = new TikaConfig();
        MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(file.getBytes()), metadata);
        if (!mediaType.toString().equals(contentType)) {
            throw new IOException("Mediatype validation failed. Passed content type is " + contentType + " but detected mediatype is " + mediaType);
        }
    }

    public void isValidType(String contentType) throws InvalidContentTypeException {
        List<String> supportedContentTypes = getSupportedContentTypes();
        boolean isMatch = PatternMatchUtils.simpleMatch(supportedContentTypes.toArray(new String[supportedContentTypes.size()]), contentType);
        if (!isMatch) {
            throw new InvalidContentTypeException("Unsupported content type for upload!");
        }
    }

}
