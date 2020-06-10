package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.repository.ImageFileRepository;
import de.terrestris.shogun.lib.util.FileUtil;
import de.terrestris.shogun.lib.util.ImageFileUtil;
import de.terrestris.shogun.properties.UploadProperties;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

@Service
public class ImageFileService extends BaseFileService<ImageFileRepository, ImageFile> {

    @Autowired
    private UploadProperties uploadProperties;

    public ImageFile create(MultipartFile uploadFile) throws Exception {

        FileUtil.validateFile(uploadFile);

        byte[] fileByteArray = FileUtil.fileToByteArray(uploadFile);

        ImageFile file = new ImageFile();
        file.setFile(fileByteArray);
        file.setFileType(uploadFile.getContentType());
        file.setFileName(uploadFile.getOriginalFilename());
        file.setActive(true);

        Dimension imageDimensions = ImageFileUtil.getImageDimensions(uploadFile);
        if (imageDimensions != null) {
            int thumbnailSize = uploadProperties.getImage().getThumbnailSize();
            file.setThumbnail(ImageFileUtil.getScaledImage(uploadFile, imageDimensions, thumbnailSize));
            file.setWidth(imageDimensions.width);
            file.setHeight(imageDimensions.height);
        } else {
            LOG.warn("Could not detect the dimensions of the image. Neither width, height " +
                "nor the thumbnail can be set.");
        }

        ImageFile savedFile = this.create(file);

        return savedFile;
    }

    public void isValidType(String contentType) throws InvalidContentTypeException {
        if (uploadProperties == null) {
            throw new InvalidContentTypeException("No properties for the upload found. " +
                "Please check your application.yml");
        }

        if (uploadProperties.getImage() == null) {
            throw new InvalidContentTypeException("No properties for the image file upload found. " +
                "Please check your application.yml");
        }

        if (uploadProperties.getImage().getSupportedContentTypes() == null) {
            throw new InvalidContentTypeException("No list of supported content types for the image file upload found. " +
                "Please check your application.yml");
        }

        List<String> supportedContentTypes = uploadProperties.getImage().getSupportedContentTypes();

        boolean isMatch = PatternMatchUtils.simpleMatch(supportedContentTypes.toArray(new String[supportedContentTypes.size()]), contentType);

        if (!isMatch) {
            throw new InvalidContentTypeException("Unsupported content type for upload!");
        }
    }

}
