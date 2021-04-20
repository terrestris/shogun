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
import de.terrestris.shogun.lib.repository.FileRepository;
import de.terrestris.shogun.lib.util.FileUtil;
import de.terrestris.shogun.properties.UploadProperties;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService extends BaseFileService<FileRepository, File> {

    @Autowired
    private UploadProperties uploadProperties;

    public File create(MultipartFile uploadFile) throws Exception {

        FileUtil.validateFile(uploadFile);

        byte[] fileByteArray = FileUtil.fileToByteArray(uploadFile);

        File file = new File();
        file.setFile(fileByteArray);
        file.setFileType(uploadFile.getContentType());
        file.setFileName(uploadFile.getOriginalFilename());
        file.setActive(true);

        File savedFile = this.create(file);

        return savedFile;
    }

    public void isValidType(String contentType) throws InvalidContentTypeException {
        if (uploadProperties == null) {
            throw new InvalidContentTypeException("No properties for the upload found. " +
                "Please check your application.yml");
        }

        if (uploadProperties.getFile() == null) {
            throw new InvalidContentTypeException("No properties for the file upload found. " +
                "Please check your application.yml");
        }

        if (uploadProperties.getFile().getSupportedContentTypes() == null) {
            throw new InvalidContentTypeException("No list of supported content types for the file upload found. " +
                "Please check your application.yml");
        }

        List<String> supportedContentTypes = uploadProperties.getFile().getSupportedContentTypes();

        boolean isMatch = PatternMatchUtils.simpleMatch(supportedContentTypes.toArray(new String[supportedContentTypes.size()]), contentType);

        if (!isMatch) {
            throw new InvalidContentTypeException("Unsupported content type for upload!");
        }
    }

}
