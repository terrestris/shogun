package de.terrestris.shogun.boot.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

@Log4j2
public class ResourceDownloader {
    public static void main(String[] args) {
        try {
            String fileUrl = args[0];
            String extractPath = args[1];

            ResourceDownloader.downloadAndExtract(fileUrl, extractPath);

            log.info("Successfully downloaded and extracted resources.");
        } catch (IOException | URISyntaxException e) {
            log.error("An error occurred: {}", e.getMessage());
            log.trace("Full stack trace: ", e);
        }
    }

    public static void downloadAndExtract(String fileUrl, String extractPath) throws IOException, URISyntaxException {
        log.info("Downloading file from URL: {}", fileUrl);

        Path downloadedFile = Files.createTempDirectory("resource-downloader-");
        try (InputStream in = new URI(fileUrl).toURL().openStream()) {
            Files.copy(in, downloadedFile, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Extracting archive contents to: {}", extractPath);

        Path extractionDir = Paths.get(extractPath);
        if (!Files.exists(extractionDir)) {
            Files.createDirectories(extractionDir);
        }

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(downloadedFile.toFile()));
             TarArchiveInputStream tarInputStream = new TarArchiveInputStream(gzipInputStream)) {
            ArchiveEntry entry;
            while ((entry = tarInputStream.getNextEntry()) != null) {
                log.info("Extracting file: {}", entry.getName());
                Path outputFile = extractionDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outputFile);
                } else {
                    Files.createDirectories(outputFile.getParent());
                    Files.copy(tarInputStream, outputFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
