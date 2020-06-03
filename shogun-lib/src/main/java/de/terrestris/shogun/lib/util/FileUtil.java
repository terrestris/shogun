package de.terrestris.shogun.lib.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class FileUtil {

    public static byte[] fileToByteArray(MultipartFile file) throws Exception {
        byte[] fileByteArray;

        try (InputStream is = file.getInputStream()) {
            fileByteArray = IOUtils.toByteArray(is);
        } catch (Exception e) {
            String msg = "Could not create bytearray from multipart file";

            log.error(msg);
            log.trace("Full stack trace: ", e);

            throw new Exception(msg);
        }

        return fileByteArray;
    }

    public static File convertToFile(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile(multipartFile.getOriginalFilename(), "");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        // Delete the file when the virtual machine terminates.
        file.deleteOnExit();

        return file;
    }

    public static void validateFile(MultipartFile file) throws Exception {
        if (file == null) {
            throw new Exception("Given file is null.");
        } else if (file.isEmpty()) {
            throw new Exception("Given file is empty.");
        }
    }

}
