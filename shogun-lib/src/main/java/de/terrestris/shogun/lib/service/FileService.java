package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.repository.FileRepository;
import de.terrestris.shogun.lib.model.File;
import javassist.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService extends BaseService<FileRepository, File> {

    @PreAuthorize("isAuthenticated()")
    public File uploadFile(MultipartFile file) throws Exception {

        if (file == null) {
            throw new Exception("Upload failed. File is null.");
        } else if (file.isEmpty()) {
            throw new Exception("Upload failed. File is empty.");
        }

        byte[] fileByteArray = null;
        File fileToPersist = new File();

        try (InputStream is = file.getInputStream()) {
            fileByteArray = IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new Exception("Could not create the bytearray: {}" + e.getMessage());
        }

        fileToPersist.setFile(fileByteArray);
        fileToPersist.setFileType(file.getContentType());
        fileToPersist.setFileName(file.getOriginalFilename());
        fileToPersist.setActive(true);

        this.repository.save(fileToPersist);

        return fileToPersist;
    }

    @PreAuthorize("isAuthenticated()")
    public File getFile (UUID fileUuid) throws NotFoundException {
        Optional<File> file = this.repository.findByFileUuid(fileUuid);

        if (file.isPresent()) {
            return file.get();
        } else {
            throw new NotFoundException("Could not find file by UUID " + fileUuid);
        }

    }
}
