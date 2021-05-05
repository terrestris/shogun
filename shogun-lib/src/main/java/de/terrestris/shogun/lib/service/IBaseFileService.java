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
import org.apache.tika.exception.TikaException;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBaseFileService<T extends BaseFileRepository<S, Long> & JpaSpecificationExecutor<S>, S extends File> {

    Optional<S> findOne(UUID fileUuid);

    S create(MultipartFile uploadFile) throws Exception;

    /**
     * Verifies if the content type of the file matches its content.
     *
     * @param file
     * @throws IOException
     * @throws TikaException
     */
    void verifyContentType(MultipartFile file) throws IOException, TikaException;

    /**
     * Checks if the given filename includes illegal characters.
     *
     * @param fileName
     * @throws InvalidFileNameException
     */
    void isValidFileName(String fileName) throws InvalidFileNameException;

    /**
     * Checks if the given contentType is included in the content type whitelist configured via UploadProperties.
     *
     * @param contentType
     * @throws InvalidContentTypeException
     */
    void isValidType(String contentType) throws InvalidContentTypeException;

    /**
     * Checks if the file is not null or empty and has a valid content type.
     *
     * @param file
     * @throws Exception
     */
    void isValid(MultipartFile file) throws Exception;

    /**
     * Get the list of supported content types. Should be implement by real class.
     *
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    List<String> getSupportedContentTypes() throws NoSuchBeanDefinitionException;
}
