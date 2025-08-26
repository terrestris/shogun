/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2025-present terrestris GmbH & Co. KG
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

/**
 * Utility class for downloading and extracting *.tar.gz resources from a given URL.
 *
 * It is currently designed to download resource assets (e.g. the Keycloak JS adapter) for the landing page during
 * the project initialization only.
 */
@Log4j2
public class ResourceDownloader {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String fileUrl = args[0];
        String extractPath = args[1];

        ResourceDownloader.downloadAndExtract(fileUrl, extractPath);
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

        log.info("Successfully downloaded and extracted resources.");
    }
}
