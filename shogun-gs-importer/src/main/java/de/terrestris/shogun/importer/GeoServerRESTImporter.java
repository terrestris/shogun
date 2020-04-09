package de.terrestris.shogun.importer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.terrestris.shogun.importer.dto.*;
import de.terrestris.shogun.importer.exception.GeoServerRESTImporterException;
import de.terrestris.shogun.lib.dto.HttpResponse;
import de.terrestris.shogun.lib.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.geotools.referencing.CRS;
import org.geotools.referencing.wkt.Formattable;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@Log4j2
public class GeoServerRESTImporter {

    private String username;

    private String password;

    private ObjectMapper mapper;

    private URI baseUri;

    public GeoServerRESTImporter() { }

    /***
     * Constructs a new importer with values set.
     */
    public GeoServerRESTImporter(URI importerBaseURL, String username,
                                 String password) {
        if (importerBaseURL == null || StringUtils.isEmpty(username) ||
            StringUtils.isEmpty(password)) {
            log.error("Missing Constructor arguments. Could not create " +
                "the GeoServerRESTImporter.");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.setSerializationInclusion(Include.NON_NULL);

        this.username = username;
        this.password = password;

        this.mapper = mapper;
        this.baseUri = importerBaseURL;
    }

    /**
     * Add a projection file to a shapefile zip archive.
     */
    public static File addPrjFileToArchive(File file, String targetCrs)
        throws ZipException, IOException, FactoryException {

        ZipFile zipFile = new ZipFile(file);

        CoordinateReferenceSystem decodedTargetCrs = CRS.decode(targetCrs);
        String targetCrsWkt = toSingleLineWKT(decodedTargetCrs);

        ArrayList<String> zipFileNames = new ArrayList<>();
        List<FileHeader> zipFileHeaders = zipFile.getFileHeaders();

        for (FileHeader zipFileHeader : zipFileHeaders) {
            if (FilenameUtils.getExtension(zipFileHeader.getFileName()).equalsIgnoreCase("prj")) {
                continue;
            }
            zipFileNames.add(FilenameUtils.getBaseName(zipFileHeader.getFileName()));
        }

        log.debug("Following files will be created and added to ZIP file: " + zipFileNames);

        for (String prefix : zipFileNames) {
            File targetPrj = null;
            try {
                targetPrj = File.createTempFile("TMP_" + prefix, ".prj");
                FileUtils.write(targetPrj, targetCrsWkt, "UTF-8");
                ZipParameters params = new ZipParameters();
//                params.setSourceExternalStream(true);
                params.setCompressionLevel(CompressionLevel.NORMAL);
                params.setFileNameInZip(prefix + ".prj");
                zipFile.addFile(targetPrj, params);
            } finally {
                if (targetPrj != null) {
                    boolean deleted = targetPrj.delete();
                    if (!deleted) {
                        log.warn("Temporary target prj file could not be deleted.");
                    }
                }
            }
        }

        return zipFile.getFile();
    }

    /**
     * Turns the CRS into a single line WKT
     *
     * @param crs CoordinateReferenceSystem which should be formatted
     * @return Single line String which can be written to PRJ file
     */
    public static String toSingleLineWKT(CoordinateReferenceSystem crs) {
        String wkt = null;
        try {
            // this is a lenient transformation, works with polar stereographics too
            Formattable formattable = (Formattable) crs;
            wkt = formattable.toWKT(0, false);
        } catch (ClassCastException e) {
            wkt = crs.toWKT();
        }

        wkt = wkt.replaceAll("\n", "").replaceAll("  ", "");
        return wkt;
    }

    /**
     * Create a new import job.
     */
    public RESTImport createImportJob(String workSpaceName, String dataStoreName)
        throws Exception {

        if (StringUtils.isEmpty(workSpaceName)) {
            throw new GeoServerRESTImporterException("No workspace given. Please provide a "
                + "workspace to import the data in.");
        }

        RESTImport importJob = new RESTImport();

        log.debug("Creating a new import job to import into workspace " + workSpaceName);

        RESTTargetWorkspace targetWorkspace = new RESTTargetWorkspace(workSpaceName);
        importJob.setTargetWorkspace(targetWorkspace);

        if (!StringUtils.isEmpty(dataStoreName)) {
            log.debug("The data will be imported into datastore " + dataStoreName);
            RESTTargetDataStore targetDataStore = new RESTTargetDataStore(dataStoreName, null);
            importJob.setTargetStore(targetDataStore);
        } else {
            log.debug("No datastore given. A new datastore will be created in relation to the"
                + "input data.");
        }

        HttpResponse httpResponse = HttpUtil.post(
            this.addEndPoint(""),
            this.asJSON(importJob),
            ContentType.APPLICATION_JSON,
            this.username,
            this.password,
            false
        );

        HttpStatus responseStatus = httpResponse.getStatusCode();
        if (responseStatus == null || !responseStatus.is2xxSuccessful()) {
            throw new GeoServerRESTImporterException("Import job cannot be "
                + "created. Error message from GeoServer is: " + new String(httpResponse.getBody()));
        }

        RESTImport restImport = (RESTImport) this.asEntity(httpResponse.getBody(), RESTImport.class);

        log.debug("Successfully created the import job with ID " + restImport.getId());

        return restImport;
    }

    /**
     * Create a reprojection task.
     */
    public boolean createReprojectTransformTask(Integer importJobId, Integer taskId,
                                                String sourceSrs, String targetSrs) throws URISyntaxException, HttpException {
        RESTReprojectTransform transformTask = new RESTReprojectTransform();
        if (StringUtils.isNotEmpty(sourceSrs)) {
            transformTask.setSource(sourceSrs);
        }
        transformTask.setTarget(targetSrs);

        return createTransformTask(importJobId, taskId, transformTask);
    }

    /**
     * Create and append importer task for <code>gdaladdo</code>
     */
    public boolean createGdalAddOverviewTask(Integer importJobId, Integer importTaskId,
                                             List<String> opts, List<Integer> levels) throws URISyntaxException, HttpException {
        RESTGdalAddoTransform transformTask = new RESTGdalAddoTransform();
        if (!opts.isEmpty()) {
            transformTask.setOptions(opts);
        }
        if (!levels.isEmpty()) {
            transformTask.setLevels(levels);
        }
        return this.createTransformTask(importJobId, importTaskId, transformTask);
    }

    /**
     * Create and append importer task for <code>gdalwarp</code>
     */
    public boolean createGdalWarpTask(Integer importJobId, Integer importTaskId,
                                      List<String> optsGdalWarp) throws URISyntaxException, HttpException {
        RESTGdalWarpTransform transformTask = new RESTGdalWarpTransform();
        if (!optsGdalWarp.isEmpty()) {
            transformTask.setOptions(optsGdalWarp);
        }
        return this.createTransformTask(importJobId, importTaskId, transformTask);
    }

    /**
     * Create and append importer task for <code>gdal_translate</code>
     */
    public boolean createGdalTranslateTask(Integer importJobId, Integer importTaskId,
                                           List<String> optsGdalTranslate) throws URISyntaxException, HttpException {
        RESTGdalTranslateTransform transformTask = new RESTGdalTranslateTransform();
        if (!optsGdalTranslate.isEmpty()) {
            transformTask.setOptions(optsGdalTranslate);
        }
        return this.createTransformTask(importJobId, importTaskId, transformTask);
    }

    /**
     * Upload an import file.
     */
    public RESTImportTaskList uploadFile(Integer importJobId, File file, String sourceSrs) throws Exception {

        log.debug("Uploading file " + file.getName() + " to import job " + importJobId);

        HttpResponse httpResponse = HttpUtil.post(
            this.addEndPoint(importJobId + "/tasks"),
            file,
            this.username,
            this.password
        );

        HttpStatus responseStatus = httpResponse.getStatusCode();
        if (responseStatus == null || !responseStatus.is2xxSuccessful()) {
            throw new GeoServerRESTImporterException("Error while uploading the file.");
        }

        log.debug("Successfully uploaded the file to import job " + importJobId);

        RESTImportTaskList importTaskList = null;
        // check, if it is a list of import tasks (for multiple layers)
        try {
            importTaskList = mapper.readValue(httpResponse.getBody(), RESTImportTaskList.class);
            log.debug("Imported file " + file.getName() + " contains data for multiple layers.");
            return importTaskList;
        } catch (IOException e) {
            log.debug("Imported file " + file.getName() + " likely contains data for single " +
                "layer. Will check this now.");
            try {
                RESTImportTask importTask = mapper.readValue(httpResponse.getBody(), RESTImportTask.class);
                if (importTask != null) {
                    importTaskList = new RESTImportTaskList();
                    importTaskList.add(importTask);
                    log.debug("Imported file " + file.getName() + " contains data for a single layer.");
                }
                return importTaskList;
            } catch (IOException ex) {
                log.info("It seems that the SRS definition source file can not be interpreted by " +
                    "GeoServer / GeoTools. Try to set SRS definition to " + sourceSrs + ".");

                File updatedGeoTiff = null;
                try {
                    if (!StringUtils.isEmpty(sourceSrs)) {
                        // "First" recursion: try to add prj file to ZIP.
                        updatedGeoTiff = addPrjFileToArchive(file, sourceSrs);
                    } else {
                        // At least second recursion: throw exception since SRS definition
                        // could not be set.
                        throw new GeoServerRESTImporterException("Could not set SRS definition "
                            + "of GeoTIFF.");
                    }
                } catch (ZipException ze) {
                    throw new GeoServerRESTImporterException("No valid ZIP file given containing "
                        + "GeoTiff datasets.");
                }

                if (updatedGeoTiff != null) {
                    importTaskList = uploadFile(importJobId, updatedGeoTiff, null);
                    return importTaskList;
                }
            }
        }

        return null;
    }

    /**
     * Updates the given import task.
     */
    public boolean updateImportTask(int importJobId, int importTaskId,
                                    AbstractRESTEntity updateTaskEntity) throws Exception {
        log.debug("Updating the import task " + importTaskId + " in job " + importJobId +
            " with " + updateTaskEntity);

        HttpResponse httpResponse = HttpUtil.put(
            this.addEndPoint(importJobId + "/tasks/" + importTaskId),
            this.asJSON(updateTaskEntity),
            ContentType.APPLICATION_JSON,
            this.username,
            this.password,
            false
        );

        boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

        if (success) {
            log.debug("Successfully updated the task " + importTaskId);
        } else {
            log.error("Unknown error occured while updating the task " + importTaskId);
        }

        return success;
    }

    /**
     * Update layer object for a given task of an import job (via PUT)
     *
     * @param importJobId      The import job ID
     * @param importTaskId     The import task ID
     * @param updateTaskEntity The entity to use for update
     * @return true if successful, false otherwise
     * @throws URISyntaxException
     * @throws HttpException
     */
    public boolean updateLayerForImportTask(int importJobId, int importTaskId, AbstractRESTEntity updateTaskEntity) throws URISyntaxException, HttpException {
        if (importJobId < 0 || importTaskId < 0) {
            log.debug("Invalid importJobId or importTaskId passed.");
            return false;
        }
        if (updateTaskEntity == null) {
            log.debug("Entity to update is null.");
            return false;
        }

        log.debug("Updating layer for the import task " + importTaskId + " in job " + importJobId + " with " + updateTaskEntity);
        HttpResponse httpResponse = HttpUtil.put(
            this.addEndPoint(importJobId + "/tasks/" + importTaskId + "/layer"),
            this.asJSON(updateTaskEntity),
            ContentType.APPLICATION_JSON,
            this.username,
            this.password,
            false
        );

        boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

        if (success) {
            log.debug("Successfully updated layer for task " + importTaskId);
        } else {
            log.error("An unknown error occurred while updating the task " + importTaskId);
        }

        return success;
    }

    /**
     * Deletes an importJob.
     */
    public boolean deleteImportJob(Integer importJobId) throws URISyntaxException, HttpException {

        log.debug("Deleting the import job " + importJobId);

        HttpResponse httpResponse = HttpUtil.delete(
            this.addEndPoint(importJobId.toString()),
            this.username,
            this.password);

        boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

        if (success) {
            log.debug("Successfully deleted the import job " + importJobId);
        } else {
            log.error("Unknown error occured while deleting the import job " + importJobId);
        }

        return success;
    }

    /**
     * Run a previously configured import job.
     */
    public boolean runImportJob(Integer importJobId) throws
        UnsupportedEncodingException, URISyntaxException, HttpException {

        log.debug("Starting the import for job " + importJobId);

        HttpResponse httpResponse = HttpUtil.post(
            this.addEndPoint(Integer.toString(importJobId)),
            this.username,
            this.password
        );

        boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

        if (success) {
            log.debug("Successfully started the import job " + importJobId);
        } else {
            log.error("Unknown error occured while running the import job " + importJobId);
        }

        return success;
    }

    /**
     * Get a layer.
     */
    public RESTLayer getLayer(Integer importJobId, Integer taskId) throws Exception {
        HttpResponse httpResponse = HttpUtil.get(
            this.addEndPoint(importJobId + "/tasks/" + taskId + "/layer"),
            this.username,
            this.password
        );

        return (RESTLayer) this.asEntity(httpResponse.getBody(), RESTLayer.class);
    }

    /**
     * fetch all created Layers of import job
     */
    public List<RESTLayer> getAllImportedLayers(Integer importJobId, List<RESTImportTask> tasks) throws Exception {
        ArrayList<RESTLayer> layers = new ArrayList<RESTLayer>();
        for (RESTImportTask task : tasks) {

            RESTImportTask refreshedTask = this.getRESTImportTask(importJobId, task.getId());
            if (refreshedTask.getState().equalsIgnoreCase("COMPLETE")) {
                HttpResponse httpResponse = HttpUtil.get(
                    this.addEndPoint(importJobId + "/tasks/" + task.getId() + "/layer"),
                    this.username,
                    this.password
                );
                RESTLayer layer = (RESTLayer) this.asEntity(httpResponse.getBody(), RESTLayer.class);

                if (layer != null) {
                    layers.add(layer);
                }
            } else if ((tasks.size() == 1) && refreshedTask.getState().equalsIgnoreCase("ERROR")) {
                throw new GeoServerRESTImporterException(refreshedTask.getErrorMessage());
            }
        }
        return layers;
    }

    /**
     * Get the data of an import task.
     */
    public RESTData getDataOfImportTask(Integer importJobId, Integer taskId)
        throws Exception {
        final DeserializationFeature unwrapRootValueFeature = DeserializationFeature.UNWRAP_ROOT_VALUE;
        boolean unwrapRootValueFeatureIsEnabled = mapper.isEnabled(unwrapRootValueFeature);

        HttpResponse httpResponse = HttpUtil.get(
            this.addEndPoint(importJobId + "/tasks/" + taskId + "/data"),
            this.username,
            this.password
        );

        // we have to disable the feature. otherwise deserialize would not work here
        mapper.disable(unwrapRootValueFeature);

        final RESTData resultEntity = (RESTData) this.asEntity(httpResponse.getBody(), RESTData.class);

        if (unwrapRootValueFeatureIsEnabled) {
            mapper.enable(unwrapRootValueFeature);
        }

        return resultEntity;
    }

    /**
     * Get an import task.
     */
    public RESTImportTask getRESTImportTask(Integer importJobId, Integer taskId) throws
        Exception {
        HttpResponse httpResponse = HttpUtil.get(
            this.addEndPoint(importJobId + "/tasks/" + taskId),
            this.username,
            this.password
        );

        return (RESTImportTask) this.asEntity(httpResponse.getBody(), RESTImportTask.class);
    }

    /**
     * @param importJobId
     * @throws Exception
     */
    public RESTImportTaskList getRESTImportTasks(Integer importJobId) throws Exception {
        HttpResponse httpResponse = HttpUtil.get(
            this.addEndPoint(importJobId + "/tasks"),
            this.username,
            this.password
        );
        return mapper.readValue(httpResponse.getBody(), RESTImportTaskList.class);
    }

    /**
     * Helper method to create an importer transformTask
     */
    private boolean createTransformTask(Integer importJobId, Integer taskId, RESTTransform transformTask)
        throws URISyntaxException, HttpException {

        log.debug("Creating a new transform task for import job" + importJobId + " and task " + taskId);

        mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);

        HttpResponse httpResponse = HttpUtil.post(
            this.addEndPoint(importJobId + "/tasks/" + taskId + "/transforms"),
            this.asJSON(transformTask),
            ContentType.APPLICATION_JSON,
            this.username,
            this.password,
            false
        );

        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        if (httpResponse.getStatusCode().equals(HttpStatus.CREATED)) {
            log.debug("Successfully created the transform task");
            return true;
        } else {
            log.error("Error while creating the transform task");
            return false;
        }
    }

    /**
     * Convert a byte array to an importer REST entity.
     */
    private AbstractRESTEntity asEntity(byte[] responseBody, Class<?> clazz)
        throws Exception {

        AbstractRESTEntity entity = null;

        entity = (AbstractRESTEntity) mapper.readValue(responseBody, clazz);

        return entity;
    }

    /**
     * Convert an object to json.
     */
    protected String asJSON(Object entity) {

        String entityJson = null;

        try {
            entityJson = this.mapper.writeValueAsString(entity);
        } catch (Exception e) {
            log.error("Could not parse as JSON: " + e.getMessage());
        }

        return entityJson;
    }

    /**
     * Add an endpoint.
     */
    protected URI addEndPoint(String endPoint) throws URISyntaxException {

        if (StringUtils.isEmpty(endPoint) || endPoint.equals("/")) {
            return this.baseUri;
        }

        if (this.baseUri.getPath().endsWith("/") || endPoint.startsWith("/")) {
            endPoint = this.baseUri.getPath() + endPoint;
        } else {
            endPoint = this.baseUri.getPath() + "/" + endPoint;
        }

        URI uri = null;

        URIBuilder builder = new URIBuilder();

        builder.setScheme(this.baseUri.getScheme());
        builder.setHost(this.baseUri.getHost());
        builder.setPort(this.baseUri.getPort());
        builder.setPath(endPoint);

        uri = builder.build();
        return uri;
    }

}
