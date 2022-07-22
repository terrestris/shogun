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
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class FileService extends BaseFileService<FileRepository, File> {

    @Autowired
    private UploadProperties uploadProperties;

    /**
     * Creates a new File entity in the Database.
     * The file content is stored as bytearray in the DB.
     *
     * @param uploadFile
     * @return
     * @throws Exception
     */
    public File create(MultipartFile uploadFile) throws Exception {

        byte[] fileByteArray = FileUtil.fileToByteArray(uploadFile);

        File file = new File();
        file.setFile(fileByteArray);
        file.setFileType(uploadFile.getContentType());
        file.setFileName(uploadFile.getOriginalFilename());
        file.setActive(true);

        File savedFile = this.create(file);

        return savedFile;
    }

    /**
     * Creates a new File entity in the Database.
     * The file is stored as file on the disk.
     *
     * @param uploadFile
     * @param writeToSystem
     * @return
     * @throws Exception
     */
    public File create(MultipartFile uploadFile, Boolean writeToSystem) throws Exception {
        if (!writeToSystem) {
            return this.create(uploadFile);
        }

        String uploadBasePath = uploadProperties.getBasePath();
        if (StringUtils.isEmpty(uploadBasePath)) {
            throw new Exception("Could not upload file. uploadBasePath is null.");
        }
        String fileName = uploadFile.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("Could not upload file. fileName is null.");
        }

        File file = new File();
        file.setFileType(uploadFile.getContentType());
        file.setFileName(fileName);
        file.setActive(true);

        File savedFile = this.create(file);

        UUID fileUuid = savedFile.getUuid();

        // Setup path and directory
        String path = fileUuid + "/" + fileName;
        java.io.File fileDirectory = new java.io.File(uploadBasePath + "/" + fileUuid);
        fileDirectory.mkdirs();

        // Write multipart file data to target directory
        byte[] fileByteArray = FileUtil.fileToByteArray(uploadFile);
        java.io.File outFile = new java.io.File(fileDirectory, fileName);
        InputStream in = new ByteArrayInputStream(fileByteArray);

        try (OutputStream out = new FileOutputStream(outFile)) {
            IOUtils.copy(in, out);
            log.info("Saved file with id {} to {}: ", savedFile.getId(), savedFile.getPath());
        } catch (Exception e) {
            log.error("Error when saving file {} to disk: " + e.getMessage(), savedFile.getId());
            log.info("Rollback creation of file {}.", savedFile.getId());
            this.repository.delete(savedFile);
            fileDirectory.delete();
            throw e;
        }

        // Update entity with saved File
        savedFile.setPath(path);
        return this.repository.save(savedFile);
    }

    @Override
    public List<String> getSupportedContentTypes() {
        if (uploadProperties == null) {
            throw new NoSuchBeanDefinitionException("No properties for the upload found. Please check your application.yml");
        }
        if (uploadProperties.getFile() == null) {
            throw new NoSuchBeanDefinitionException("No properties for the file upload found. Please check your application.yml");
        }
        if (uploadProperties.getFile().getSupportedContentTypes() == null) {
            throw new NoSuchBeanDefinitionException("No list of supported content types for the file upload found. " +
                "Please check your application.yml");
        }
        return uploadProperties.getFile().getSupportedContentTypes();
    }
}
