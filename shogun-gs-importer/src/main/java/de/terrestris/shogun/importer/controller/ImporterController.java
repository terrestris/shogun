package de.terrestris.shogun.importer.controller;

import de.terrestris.shogun.importer.exception.GeoServerRESTImporterException;
import de.terrestris.shogun.importer.service.ImporterService;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.Map;

@Controller
@RequestMapping("/import")
@Log4j2
public class ImporterController {

    @Autowired
    private ImporterService service;

    /**
     * <p>createLayer.</p>
     *
     * @param file
     * @param fileProjection a {@link java.lang.String} object.
     * @param dataType       a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/create-layer.action", method = {RequestMethod.POST})
    public ResponseEntity<?> createLayer(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "fileProjection", required = false) String fileProjection,
        @RequestParam("dataType") String dataType) {

        log.debug("Requested to create a layer from geo-file(s).");

        try {
            if (file.isEmpty()) {
                log.error("Upload failed. File " + file + " is empty.");
                throw new GeoServerRESTImporterException("File " +
                    file.getOriginalFilename() + " is empty.");
            }

            Map<String, Object> responseMap = this.service.importGeodataAndCreateLayer(
                file,
                fileProjection,
                dataType
            );

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            log.error("Could not create layer: " + e.getMessage());
            log.trace("Full stack trace ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * <p>importWfs.</p>
     *
     * @param wfsUrl      The base URL of the WFS server to fetch the features from,
     *                    e.g. "http://geoserver:8080/geoserver/ows". Required.
     * @param wfsVersion  The WFS version to use, possible values are usually one of
     *                    1.0.0, 1.1.0 or 2.0.0. Optional.
     * @param featureType The name of the featureType to fetch and import, e.g.
     *                    "GDA_Wasser:OG_MESSSTELLEN_NETZ_BESCHRIFTUNG". Required.
     * @param targetEpsg  The EPSG to be used for the imported features, e.g. "EPSG:3857".
     *                    Optional.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/wfs.action", method = {RequestMethod.POST})
    public ResponseEntity<?> importWfs(
        @RequestParam(value = "wfsUrl", required = true) String wfsUrl,
        @RequestParam(value = "wfsVersion", required = false) String wfsVersion,
        @RequestParam(value = "featureType", required = true) String featureType,
        @RequestParam(value = "targetEpsg", required = false) String targetEpsg) {

        log.debug("Requested to import featureType " + featureType + " from WFS server " + wfsUrl +
            " (in version " + wfsVersion + " and projection " + targetEpsg + ") as new layer.");

        try {
            // TODO
//            ProjectLayer createdLayer = this.service.importWfsAndCreateLayer(
//                wfsUrl, wfsVersion, featureType, targetEpsg);
            String createdLayer = null;

            return ResponseEntity.ok(createdLayer);
        } catch (Exception e) {
            String errMsg = "Error while creating a layer from WFS GetCapabilities";
            log.error(errMsg + ": ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * <p>updateCrsForImport.</p>
     *
     * @param layerName      a {@link java.lang.String} object.
     * @param dataType       a {@link java.lang.String} object.
     * @param importJobId    a {@link java.lang.Integer} object.
     * @param taskId         a {@link java.lang.Integer} object.
     * @param fileProjection a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/update-crs-for-import.action", method = {RequestMethod.POST})
    public ResponseEntity<?> updateCrsForImport(
        @RequestParam("layerName") String layerName,
        @RequestParam("dataType") String dataType,
        @RequestParam("importJobId") Integer importJobId,
        @RequestParam("taskId") Integer taskId,
        @RequestParam(value = "fileProjection") String fileProjection) {

        try {
            Map<String, Object> responseMap = this.service.updateCrsForImport(
                layerName,
                dataType,
                importJobId, taskId, fileProjection);

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            log.error("updateCrsForImport has thrown an exception. Error was: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * <p>deleteImportJob.</p>
     *
     * @param importJobId a {@link java.lang.Integer} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/delete-import-job.action", method = {RequestMethod.POST})
    public ResponseEntity<?> deleteImportJob(@RequestParam("importJobId") Integer importJobId) {

        try {
            Map<String, Object> responseMap = this.service.deleteImportJob(importJobId);

            return ResponseEntity.ok(responseMap);
        } catch (URISyntaxException | HttpException e) {
            log.error("deleteImportJob has thrown an exception. Error was: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
