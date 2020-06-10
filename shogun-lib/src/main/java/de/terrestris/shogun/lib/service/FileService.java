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
