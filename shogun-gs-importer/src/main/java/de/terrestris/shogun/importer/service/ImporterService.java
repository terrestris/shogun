package de.terrestris.shogun.importer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import de.terrestris.shogun.importer.GeoServerRESTImporter;
import de.terrestris.shogun.importer.config.properties.ImporterProperties;
import de.terrestris.shogun.importer.dto.*;
import de.terrestris.shogun.importer.exception.GeoServerRESTImporterException;
import de.terrestris.shogun.importer.transformer.VectorFeatureTypeTransformer;
import de.terrestris.shogun.importer.validator.TableNameValidator;
import de.terrestris.shogun.lib.dto.HttpResponse;
import de.terrestris.shogun.lib.enumeration.LayerType;
import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.util.HttpUtil;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.geotools.data.*;
import org.geotools.data.crs.ReprojectFeatureResults;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Log4j2
public class ImporterService {

    private static final String EXPECTED_CSV_ENCODING = "UTF-8";
    private static final String NAME_OF_ABSCISSA = "X";
    private static final String NAME_OF_ORDINATE = "Y";
    private static final String NAME_OF_GEOMETRY = "the_geom";
    private static final String GEOTOOLS_PT_HEADER = NAME_OF_GEOMETRY + ":Point";
    private static final String DEFAULT_WFS_VERSION = "1.1.0";
    private static final String WFS_MAX_FEATURES = "0"; // 0 = no limit
    private static final String WFS_IMPORT_TABLE_PREFIX = "WFS_IMP";
    private static char CSVFIELDDIVIDERCHAR = ';';
    private static char CSVQUOTECHAR = '\'';

    @Autowired
    private GeoServerRESTImporter geoServerImporter;

    @Autowired
    private GeoServerRESTManager geoServerManager;

    @Autowired
    private ImporterProperties importerProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KeycloakUtil keycloakUtil;

    /**
     * @param file
     * @param fileProjection
     * @param layerType
     * @return
     * @throws Exception
     */
    public Map<String, Object> importGeodataAndCreateLayer(
        MultipartFile file,
        String fileProjection,
        String layerType
    ) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        // 1. Check if a CSV file has been uploaded. If true => transform to shapefile.
        if (StringUtils.containsIgnoreCase(file.getContentType(), "text/csv") ||
            StringUtils.containsIgnoreCase(file.getContentType(), "text/plain")) {
            file = this.createShapeFileForCsv(file);
        } else if (layerType.equalsIgnoreCase("vector")) {
            // 2. Transform to geometry to 2D if 3D data is contained
            file = reduceTo2d(file);
        }

        // 3. Create the import job.
        RESTImport restImport = null;
        if (layerType.equalsIgnoreCase("vector")) {
            restImport = this.geoServerImporter.createImportJob(
                this.importerProperties.getTargetWorkspace(),
                this.importerProperties.getVector().getTargetDatastore()
            );
        } else if (layerType.equalsIgnoreCase("raster")) {
            restImport = this.geoServerImporter.createImportJob(
                this.importerProperties.getTargetWorkspace(), null);
        } else {
            throw new GeoServerRESTImporterException("Invalid layerType given. " +
                "Valid options are: vector, raster");
        }

        // 4. Upload the import file.
        Integer importJobId = restImport.getId();
        RESTImportTaskList importTasks = null;
        importTasks = this.uploadZipFile(importJobId, file, fileProjection);

        if (importTasks == null) {
            throw new GeoServerRESTImporterException("Import task could not be created.");
        }

        // 5. Check if we need to set the SRS of the import layer manually.
        RESTImportTaskList tasksWithoutProjection = new RESTImportTaskList(); //this.checkSrsOfImportTasks(importTasks);

        // 6. Add transform tasks.
        try {
            createTransformTasks(fileProjection, layerType, importJobId, importTasks);
        } catch (GeoServerRESTImporterException gsrie) {
            // TODO what happens if more than one importTasks are contained here?
            tasksWithoutProjection.addAll(importTasks);
        }

        // Redefine broken tasks
        if (tasksWithoutProjection.size() > 0 && StringUtils.isEmpty(fileProjection)) {
            responseMap.put("success", false);
            responseMap.put("message", "NO_CRS or invalid CRS (EPSG:404000) detected and "
                + "no fileProjection is given.");
            responseMap.put("importJobId", importJobId);
            responseMap.put("tasksWithoutProjection", tasksWithoutProjection);
            responseMap.put("error", "NO_CRS");

            return responseMap;
        }

        // 7. Run the import and create the SHOGun layer.
        responseMap = runJobAndCreateLayer(file.getOriginalFilename(), layerType, importJobId, false);

        return responseMap;
    }

    /**
     * Content of the shapefile within the ZIP will be transformed to 2D
     *
     * @param shapeFile
     * @return
     * @throws Exception
     */
    private MultipartFile reduceTo2d(MultipartFile shapeFile) throws Exception {
        final String outputFolderPath = FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis();
        final File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        final String originalFileName = shapeFile.getOriginalFilename();
        File file = new File(outputFolder + File.separator + originalFileName);
        shapeFile.transferTo(file);
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(outputFolderPath);

        Collection<File> containedShapes = FileUtils.listFiles(outputFolder, new String[]{"shp"}, false);
        File firstMatchingFile = containedShapes.stream().findFirst().get();
        FileDataStore store = FileDataStoreFinder.getDataStore(firstMatchingFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // check if 2D
        final SimpleFeatureType featureType = store.getSchema();
        GeometryDescriptor geomDescr = featureType.getGeometryDescriptor();
        ShapeType shapeType = JTSUtilities.getShapeType(geomDescr);
        log.debug("Shapefile has following shape type: " + shapeType);

        log.info("Shapefile contains 3D data. Will map data to 2D.");
        // read in the 2D geometries only
        Query query = new Query("features");
        query.setHints(new Hints(Hints.FEATURE_2D, true));
        SimpleFeatureCollection featureCollection = featureSource.getFeatures(query);
        return createShapeZipForFeatureCollection(featureType.getTypeName(), featureType, featureCollection);
    }

    /**
     * Generate Shapefile (ZIP) containing points (ONLY!) based on CSV
     *
     * @param uploadedCsv {@link MultipartFile} which contains CSV
     * @return {@link MultipartFile} containing ZIP file which shape (POINT)
     * @throws Exception
     */
    private MultipartFile createShapeFileForCsv(MultipartFile uploadedCsv) throws Exception {
        MultipartFile createdShapeZip;

        long csvFileSize = uploadedCsv.getSize();
        String csvFileName = uploadedCsv.getOriginalFilename();
        String csvFileContentType = uploadedCsv.getContentType();
        log.info("Trying to transform features of " + csvFileName);
        log.debug("The Content-Type of the file is " + csvFileContentType + ". The file-size is " + csvFileSize + " bytes.");
        String csvFileNameWithoutExtension = csvFileName;
        if (csvFileNameWithoutExtension.contains(".")) {
            csvFileNameWithoutExtension = csvFileNameWithoutExtension.substring(0, csvFileNameWithoutExtension.lastIndexOf("."));
        }

        Transaction transaction = null;

        try (
            InputStream is = uploadedCsv.getInputStream();
            // TODO get rid of deprecated constructor
            CSVReader reader = new CSVReader(new InputStreamReader(is, EXPECTED_CSV_ENCODING), CSVFIELDDIVIDERCHAR, CSVQUOTECHAR);
        ) {
            // HashMap to store the entries of the CSV file in
            // Each line results in an entry of the HashMap
            HashMap<String, HashMap<String, String>> retVals = new HashMap<>();

            String[] nextLine;
            int lineCounter = 0;
            String[] header = null;

            while ((nextLine = reader.readNext()) != null) {
                if (lineCounter == 0) {
                    header = nextLine;
                    lineCounter++;
                    continue;
                }
                HashMap<String, String> inlineObject = new HashMap<String, String>();

                if (header.length != nextLine.length) {
                    // this line will be ignored from import
                    continue;
                }

                for (int i = 0; i < nextLine.length; i++) {
                    inlineObject.put(header[i].trim().replace("'", "").toLowerCase(),
                        nextLine[i].trim().replace("'", ""));
                }
                retVals.put("Object_" + lineCounter, inlineObject);
                lineCounter++;
            }

            String schemaName = "IMP_" + System.currentTimeMillis() + "_" + csvFileNameWithoutExtension;
            if (!TableNameValidator.isValidName(schemaName)) {
                schemaName = TableNameValidator.createValidTableName(schemaName);
            }

            // create feature type and feature collection
            SimpleFeatureType featureTypeDefinition = createFeatureType(header, schemaName);
            SimpleFeatureCollection featureCollection = createFeatureCollectionFromCsv(featureTypeDefinition, retVals);

            createdShapeZip = createShapeZipForFeatureCollection(schemaName, featureTypeDefinition, featureCollection);
        } finally {
            // TODO this log is probably wrong because because we can also come to the finally
            // block when an exception has been catched
            log.debug("Created ZIP file containing shapefile based on CSV successfully.");
            IOUtils.closeQuietly(transaction);
        }

        return createdShapeZip;
    }

    /**
     * @param schemaName
     * @param featureTypeDefinition
     * @param featureCollection
     * @return
     * @throws IOException
     */
    private MultipartFile createShapeZipForFeatureCollection(String schemaName, SimpleFeatureType featureTypeDefinition, FeatureCollection featureCollection) throws IOException {
        // Define temporary directories to put the shapefile in
        File tmpDirBase = FileUtils.getTempDirectory();
        String tmpDirBasePath = tmpDirBase.getAbsolutePath();
        File newFile = new File(tmpDirBasePath + "/" + schemaName + ".shp");
        Map<String, Serializable> params = new HashMap<>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        // create shapefile using datastore factory
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore shapefileDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        shapefileDataStore.createSchema(featureTypeDefinition);

        /*
         * Write the features to the shapefile
         */
        DefaultTransaction transaction = new DefaultTransaction("create");
        String typeName = shapefileDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = shapefileDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();

        log.debug("Write temporary shapefile -- SHAPE:" + SHAPE_TYPE);
        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(featureCollection);
                transaction.commit();
            } catch (Exception problem) {
                log.debug("Could not add features to shapefile", problem);
                transaction.rollback();
            } finally {
                transaction.close();
            }
            log.info("Successfully created shapefile. Will now pack it to a ZIP file...");
            return createShapeZip(tmpDirBasePath, schemaName);
        } else {
            throw new IOException("Could not create appropriate shapefile datastore");
        }
    }

    /**
     * Generate multipart file (ZIP) for shapefile
     *
     * @param tempDir   path to temporary directory
     * @param shapeName Name pof the shapefile
     * @return MultipartFile representing ZIP file which contains parts of shapefile
     * @throws IOException
     */
    private MultipartFile createShapeZip(String tempDir, String shapeName) throws IOException {
        final String basePath = tempDir + "/";
        final String zipFileName = basePath + shapeName + ".zip";
        try (
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
        ) {
            // only "shp", "dbf", "shx" should be included in ZIP file so that the user must set CRS manually
            for (String ending : new String[]{"shp", "dbf", "shx", "prj"}) {
                String fileName = basePath + shapeName + "." + ending;
                File file = new File(fileName);
                try (
                    FileInputStream fis = new FileInputStream(file);
                ) {
                    ZipEntry zipEntry = new ZipEntry(shapeName + "." + ending);
                    zos.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                } finally {
                    log.trace("Added " + fileName + " to ZIP file");
                }
            }
            zos.finish();
        } finally {
            final File zipFile = new File(zipFileName);
            final DiskFileItem diskFileItem = new DiskFileItem(
                "file",
                "application/zip",
                true,
                zipFile.getName(),
                100000000,
                zipFile.getParentFile()
            );

            InputStream input = new FileInputStream(zipFile);
            OutputStream os = diskFileItem.getOutputStream();
            IOUtils.copy(input, os);
            os.close();

            return new CommonsMultipartFile(diskFileItem);
        }
    }

    /**
     * Helper method creating feature collection
     *
     * @param featureTypeDefinition {@link SimpleFeatureType} of provided features
     * @param retVals               {@link HashMap} representing features
     * @return {@link SimpleFeatureCollection} feature collection
     */
    private SimpleFeatureCollection createFeatureCollectionFromCsv(SimpleFeatureType featureTypeDefinition, HashMap<String, HashMap<String, String>> retVals) {
        ArrayList<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
        PrecisionModel precModel = new PrecisionModel(PrecisionModel.FLOATING);
        GeometryFactory geomFactory = new GeometryFactory(precModel);

        long internalId = 1;
        for (String key : retVals.keySet()) {
            String x, y;

            HashMap<String, String> geoObject = retVals.get(key);
            x = geoObject.get(NAME_OF_ABSCISSA.toLowerCase());
            y = geoObject.get(NAME_OF_ORDINATE.toLowerCase());

            if (StringUtils.isBlank(x) || StringUtils.isBlank(y)) {
                log.warn("Object " + geoObject + " will be ignored from import since one/more coordinate(s) are empty.");
                continue;
            }

            double xVal, yVal;
            try {
                xVal = Double.parseDouble(x);
                yVal = Double.parseDouble(y);
            } catch (NumberFormatException nfe) {
                log.warn("Object " + geoObject
                    + " will be ignored from import since one/more cordinates don't contain numerical values.");
                continue;
            }

            Point pt = geomFactory.createPoint(new Coordinate(xVal, yVal));
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureTypeDefinition);

            // Iterate over featureType
            List<AttributeDescriptor> attrDescrList = featureTypeDefinition.getAttributeDescriptors();
            for (AttributeDescriptor attrDesc : attrDescrList) {
                Name attributeName = attrDesc.getName();
                if (attributeName.getLocalPart().equalsIgnoreCase(NAME_OF_GEOMETRY)) {
                    featureBuilder.add(pt);
                } else {
                    String attrVal = geoObject.get(attributeName.getLocalPart().toLowerCase());
                    featureBuilder.add(attrVal);
                }
            }

            SimpleFeature feature = featureBuilder.buildFeature(Long.toString(internalId));
            featureList.add(feature);

            internalId++;
        }

        SimpleFeatureCollection featureCollection = DataUtilities.collection(featureList);
        return featureCollection;
    }

    /**
     * Generate feature type based on header information of CSV file
     * Assumptions:
     * * header row available
     * * header contains X and Y
     *
     * @param header     String array with header description
     * @param schemaName Name of shapefile
     * @return SimpleFeature
     * @throws SchemaException
     */
    private SimpleFeatureType createFeatureType(String[] header, String schemaName) throws SchemaException {

        SimpleFeatureType featureTypeDefinition = null;

        StringBuffer headerSb = new StringBuffer();
        boolean containsXY = false;
        for (String part : header) {

            if (part.equalsIgnoreCase(NAME_OF_ORDINATE) || part.equalsIgnoreCase(NAME_OF_ABSCISSA)) {
                containsXY = containsXY || part.equalsIgnoreCase(NAME_OF_ORDINATE)
                    || part.equalsIgnoreCase(NAME_OF_ABSCISSA);
                continue;
            }

            headerSb.append(part.toLowerCase() + ":String");
            headerSb.append(",");
        }

        // Geometry needs to be at the first place
        if (containsXY) {
            headerSb.insert(0, GEOTOOLS_PT_HEADER + ",");
            headerSb = headerSb.deleteCharAt(headerSb.length() - 1);
            featureTypeDefinition = DataUtilities.createType(schemaName, headerSb.toString());
        }
        return featureTypeDefinition;
    }

    /**
     * @param importJobId
     * @param uploadFile
     * @return
     * @throws Exception
     */
    public RESTImportTaskList uploadZipFile(Integer importJobId, MultipartFile uploadFile,
                                            String fileProjection) throws Exception {
        File file = File.createTempFile("TMP_SHOGUN_UPLOAD_", uploadFile.getOriginalFilename());
        uploadFile.transferTo(file);
        RESTImportTaskList importTaskList = null;

        try {
            importTaskList = this.geoServerImporter.uploadFile(importJobId, file, fileProjection);
        } finally {
            file.delete();
        }

        return importTaskList;
    }

    /**
     * Save the layer in SHOGun
     *
     * @param layerName     The layer name
     * @param layerDataType The datatype of the layer
     * @return
     */
    public Layer saveLayer(String layerName, String layerDataType) throws JsonProcessingException, UnsupportedEncodingException, HttpException, URISyntaxException, GeoServerRESTImporterException {
        Layer layer = new Layer();
        layer.setName(layerName);
        layer.setType(LayerType.WMS);

        Map<String, Object> sourceConfig = new HashMap<>();
        sourceConfig.put("url", this.importerProperties.getInterceptorEndpoint());
        sourceConfig.put("layerName", this.importerProperties.getTargetWorkspace() + ":" + layerName);

        layer.setSourceConfig(sourceConfig);

        String serializedLayer = objectMapper.writeValueAsString(layer);

        // Save the layer
        HttpResponse response = HttpUtil.post(
            String.format("%s/layers", this.importerProperties.getShogun().getBaseUrl()),
            serializedLayer,
            ContentType.APPLICATION_JSON,
            this.importerProperties.getShogun().getUsername(),
            this.importerProperties.getShogun().getPassword(),
            false
        );

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new GeoServerRESTImporterException("Could not save layer in SHOGun: " + new String(response.getBody()));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        keycloakUtil.getKeycloakUserIdFromAuthentication(authentication);

//        projectLayerService.addAndSaveUserPermissions(layer, currentUser, Permission.ADMIN);
//
//        // Set the userpermissions for the newly created layer appearance.
//        layerAppearanceService.addAndSaveUserPermissions(appearance, currentUser, Permission.ADMIN);
//
//        // Now insert special rules to always modify any OGC requests.
//        this.projectInterceptorRuleService.createAllRelevantOgcRules(endpoint, RuleType.MODIFY);

        return layer;
    }

    /**
     * @param fileProjection
     * @param layerType
     * @param importJobId
     * @param importTasks
     * @throws Exception
     */
    private void createTransformTasks(String fileProjection, String layerType, Integer importJobId, RESTImportTaskList importTasks) throws Exception {

        for (RESTImportTask importTask : importTasks) {
            importTask = this.geoServerImporter.getRESTImportTask(importJobId, importTask.getId());
            if (importTask.getState().equalsIgnoreCase("NO_CRS")) {
                if (StringUtils.isEmpty(fileProjection)) {
                    throw new GeoServerRESTImporterException(
                        "Task state is \"NO_CRS\" and no custom projection found.");
                }

                log.debug("Try to set CRS definition for import task " + importTask.getId()
                    + " of import job " + importJobId + " to " + fileProjection);

                Integer importTaskId = importTask.getId();
                RESTLayer updateLayer = new RESTLayer();
                updateLayer.setSrs(fileProjection);

                this.geoServerImporter.updateImportTask(importJobId, importTaskId, updateLayer);
            }

            Integer importTaskId = importTask.getId();

            log.debug("Successfully created Task with ID " + importTaskId + " for ImportJob "
                + importJobId);

            if (layerType.equalsIgnoreCase("raster")) {
                if (StringUtils.isEmpty(fileProjection)) {
                    fileProjection = this.geoServerImporter.getLayer(importJobId, importTaskId).getSrs();
                    if (StringUtils.isEmpty(fileProjection)) {
                        String errMsg = "Could not determine the projection of the provided dataset, "
                            + "please update the import job " + importJobId + " with an CRS to use.";
                        log.debug(errMsg);
                        throw new GeoServerRESTImporterException(errMsg);
                    }
                    if (StringUtils.equalsIgnoreCase(fileProjection, "EPSG:404000")) {
                        String errMsg = "No valid CRS could be determined (\"EPSG:404000\"), "
                            + "please update the import job " + importJobId + " with an CRS to use.";
                        log.debug(errMsg);
                        throw new GeoServerRESTImporterException(errMsg);
                    }
                }

                // Calculate image transformation.
                if (this.importerProperties.getRaster().getPerformGdalWarp()) {
                    log.debug("Perform gdalwarp transform to target SRS during import");

                    ArrayList<String> optsGdalWarp = new ArrayList<String>();
                    optsGdalWarp.add("-s_srs");
                    optsGdalWarp.add(fileProjection);

                    optsGdalWarp.add("-t_srs");
                    optsGdalWarp.add(this.importerProperties.getTargetEPSG());

                    boolean warpTaskSuccess = this.geoServerImporter.createGdalWarpTask(importJobId, importTaskId, optsGdalWarp);
                    if (warpTaskSuccess) {
                        log.debug("Successfully created the GdalWarpTask.");
                    } else {
                        log.error("Could not create GdalWarpTask.");
                    }
                }

                if (this.importerProperties.getRaster().getPerformGdalAddo()) {
                    log.debug("Perform gdaladdo transform for levels: " + this.importerProperties.getRaster().getGdalAddoLevels());
                    List<String> optsGdalAddo = Arrays.asList(new String[]{"-r", "cubic"});

                    Boolean gdalAddoTaskSuccess = this.geoServerImporter.createGdalAddOverviewTask(importJobId, importTaskId,
                        optsGdalAddo, this.importerProperties.getRaster().getGdalAddoLevels());
                    if (gdalAddoTaskSuccess) {
                        log.debug("Successfully created the gdalAddoTask.");
                    } else {
                        log.error("Could not create gdalAddoTask.");
                    }
                }

            } else if (layerType.equalsIgnoreCase("vector")) {
                log.debug("Create ReprojectTransformTask for vector layer");
                Boolean transformTask = this.geoServerImporter.createReprojectTransformTask(importJobId,
                    importTaskId, fileProjection, this.importerProperties.getTargetEPSG());
                if (transformTask) {
                    log.debug("Successfully created the TransformTask.");
                } else {
                    log.error("Could not create the TransformTask.");
                }
            }
        }
    }

    /**
     * @param layerName
     * @param layerType
     * @param importJobId
     * @return
     * @throws Exception
     */
    private Map<String, Object> runJobAndCreateLayer(String layerName, String layerType, Integer importJobId, Boolean updateBbox) throws Exception {
        try {
            Map<String, Object> responseMap = new HashMap<>();

            Boolean respImp = false;
            try {
                HttpUtil.setHttpTimeout(this.importerProperties.getHttpTimeout());
                // Run the import job (does not depend on layerType).
                respImp = this.geoServerImporter.runImportJob(importJobId);
            } finally {
                HttpUtil.resetHttpTimeout();
            }

            if (respImp) {
                log.info("Successfully run the Import Job with ID " + importJobId);
            } else {
                log.error("Error while running the import job.");
                responseMap.put("success", false);
                responseMap.put("message", "Error while running the import job.");
            }

            // at this point will persist/return the layer of the first successful import task
            // since addition of multiple layers is not implemented in MM admin yet.
            RESTLayer restLayer = null;
            RESTImportTaskList restImportTasks = this.geoServerImporter.getRESTImportTasks(importJobId);
            for (RESTImportTask restImportTask : restImportTasks) {
                if (restImportTask.getState().equalsIgnoreCase("COMPLETE")) {
                    restLayer = this.geoServerImporter.getLayer(importJobId, restImportTask.getId());
                    break;
                }
                if (restImportTask.getState().equalsIgnoreCase("ERROR")) {
                    RESTImportTask importTask = this.geoServerImporter.getRESTImportTask(importJobId, restImportTask.getId());
                    log.error("Error while processing task with ID " + importTask.getId() + ". Error msg:" +
                        importTask.getErrorMessage());
                    throw new GeoServerRESTImporterException("Could not import dataset. Please contact admin");
                }
            }

            if (restLayer != null) {

                // If we had to override/force the CRS for an uploaded file, we might need to update
                // the CRS in GeoServer.
                // TODO Don't check for getPerformGdalWarp!!!!
                if (this.importerProperties.getRaster().getPerformGdalWarp()) {
                    updateReferencingForLayer(restLayer, updateBbox);
                }

                Layer layer = this.saveLayer(restLayer.getName(), layerType);

                responseMap.put("success", true);
                responseMap.put("data", layer);
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "No layer of imported dataset could be imported.");
            }

            return responseMap;
        } finally {
            try {
                if (layerType.toLowerCase().equals("vector")) {
                    this.deleteTemporaryShapeFiles(importJobId);
                }
            } catch (Exception e) {
                log.info("Could not delete Layer data on the file system for layer: "
                    + layerName);
            }
        }
    }

    /**
     * @param importTasks
     * @return
     */
    private RESTImportTaskList checkSrsOfImportTasks(RESTImportTaskList importTasks) {
        RESTImportTaskList tasksWithoutProjection = new RESTImportTaskList();
        for (RESTImportTask importTask : importTasks) {
            if (!importTaskHasCrs(importTask)) {
                tasksWithoutProjection.add(importTask);
                log.debug("NO_CRS for importTask " + importTask.getId() + " found.");
            }
        }
        return tasksWithoutProjection;
    }

    /**
     * TODO: support all error codes:
     * <p>
     * PENDING, READY, RUNNING, NO_CRS, NO_BOUNDS, NO_FORMAT, BAD_FORMAT, ERROR, CANCELED, COMPLETE
     *
     * @param importTask
     * @return
     */
    @SuppressWarnings("static-method")
    private boolean importTaskHasCrs(RESTImportTask importTask) {
        return !importTask.getState().equalsIgnoreCase("NO_CRS");
    }

    /**
     * @param restLayer
     */
    private boolean updateReferencingForLayer(RESTLayer restLayer, Boolean updateBbox) throws URISyntaxException, HttpException, GeoServerRESTImporterException {

        log.debug("Updating the referencing configuration of layer: " + restLayer.getName());

        boolean success = false;

        it.geosolutions.geoserver.rest.decoder.RESTLayer gsLayer = this.geoServerManager.getReader().getLayer(
            this.importerProperties.getTargetWorkspace(), restLayer.getName());

        if (gsLayer == null) {
            throw new GeoServerRESTImporterException("Could not find layer " + restLayer.getName());
        }

        if (gsLayer.getTypeString().equalsIgnoreCase("raster")) {
            RESTCoverage gsCoverage = this.geoServerManager.getReader().getCoverage(gsLayer);

            String nativeCrs = gsCoverage.getNativeCRS();
            if (!nativeCrs.equalsIgnoreCase(this.importerProperties.getTargetEPSG())) {
                GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                coverageEncoder.setNativeCRS(this.importerProperties.getTargetEPSG());
                coverageEncoder.setSRS(this.importerProperties.getTargetEPSG());
                coverageEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED);

                // update bbox as well
                if (updateBbox) {
                    final double maxX = gsCoverage.getNativeBoundingBox().getMaxX();
                    final double minX = gsCoverage.getNativeBoundingBox().getMinX();
                    final double maxY = gsCoverage.getNativeBoundingBox().getMaxY();
                    final double minY = gsCoverage.getNativeBoundingBox().getMinY();
                    coverageEncoder.setNativeBoundingBox(minX, minY, maxX, maxY, this.importerProperties.getTargetEPSG());
                }

                String coverageStoreName = gsCoverage.getStoreName();
                if (StringUtils.contains(coverageStoreName, this.importerProperties.getTargetWorkspace())) {
                    coverageStoreName = StringUtils.replace(coverageStoreName,
                        this.importerProperties.getTargetWorkspace() + ":", "");
                }

                success = this.geoServerManager.getPublisher().configureCoverage(coverageEncoder,
                    this.importerProperties.getTargetWorkspace(),
                    coverageStoreName, gsCoverage.getName());
            }
        } else if (gsLayer.getTypeString().equalsIgnoreCase("vector")) {
            RESTFeatureType gsFeatureType = this.geoServerManager.getReader().getFeatureType(gsLayer);

            if (!gsFeatureType.getNativeCRS().equalsIgnoreCase(this.importerProperties.getTargetEPSG())) {
                GSFeatureTypeEncoder featureTypeEncoder = new GSFeatureTypeEncoder();
                featureTypeEncoder.setNativeCRS(this.importerProperties.getTargetEPSG());
                featureTypeEncoder.setSRS(this.importerProperties.getTargetEPSG());
                featureTypeEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED);

                String dataStoreName = gsFeatureType.getStoreName();
                if (StringUtils.contains(dataStoreName, this.importerProperties.getTargetWorkspace())) {
                    dataStoreName = StringUtils.replace(dataStoreName,
                        this.importerProperties.getTargetWorkspace() + ":", "");
                }

                success = this.configureFeatureType(featureTypeEncoder, this.importerProperties.getTargetWorkspace(),
                    dataStoreName, gsFeatureType.getName());
            }
        } else {
            log.debug("Unknown layer type given. Could not check if we had to update the CRS.");
        }

        if (success) {
            log.debug("Successfully updated the referencing configuration of the layer.");
        } else {
            log.error("Could not update the referencing configuration of the layer.");
        }

        return success;
    }

    /**
     * Imports the features of a given featureType out of the provided WFS server into
     * the database and creates a GeoServer and SHOGun layer based on it.
     *
     * @param wfsUrl          The base URL of the WFS server to fetch the features from,
     *                        e.g. "http://geoserver:8080/geoserver/ows". Required.
     * @param wfsVersion      The WFS version to use, possible values are usually one of
     *                        1.0.0, 1.1.0 or 2.0.0. If not set, {@link #DEFAULT_WFS_VERSION} will be used.
     * @param featureTypeName The name of the featureType to fetch and import,
     *                        e.g. "GDA_Wasser:OG_MESSSTELLEN_NETZ_BESCHRIFTUNG". Required.
     * @param targetEpsg      The EPSG to be used for the imported features, e.g. "EPSG:3857".
     *                        If not set, geoServerDefaultSrs will be used.
     * @return The created SHOGun layer.
     * @throws GeoServerRESTImporterException
     */
    // TODO Return layer
    public void importWfsAndCreateLayer(String wfsUrl, String wfsVersion,
                                                String featureTypeName, String targetEpsg) throws GeoServerRESTImporterException {

        WFSDataStore wfsDataStore = null;

        if (StringUtils.isEmpty(wfsVersion)) {
            log.debug("No WFS version given, will use the default version " + DEFAULT_WFS_VERSION);
            wfsVersion = DEFAULT_WFS_VERSION;
        }

        if (StringUtils.isEmpty(targetEpsg)) {
            log.debug("No targetEpsg given, it will be set to the default one: " +
                this.importerProperties.getTargetEPSG());
            targetEpsg = this.importerProperties.getTargetEPSG();
        }

        try {
            // 1. Create a WFS datastore that contains the given connection params.
            wfsDataStore = this.createWfsDataStore(wfsUrl, wfsVersion);
            // 2. Fetch the features from the given featureType using the store created above.
            SimpleFeatureCollection featureCollection = this.getFeatureCollectionFromDataStore(
                wfsDataStore, featureTypeName, targetEpsg);
            // 3. Persist the featureCollection in the database.
            String tableName = this.importFeatureCollectionToDatabase(featureCollection);
            // 4. Publish the layer based on the DB table in GeoServer.
            this.publishGeoServerLayerFromDbTable(tableName, featureTypeName, targetEpsg);
            // 5. Persist the layer entitiy in SHOGun DB.
            // TODO HTTP
//            ProjectLayer layer = this.saveLayer(tableName.toUpperCase(), "vector");

//            return layer;
        } finally {
            if (wfsDataStore != null) {
                wfsDataStore.dispose();
            }
        }
    }

    /**
     * Creates a {@link WFSDataStore} based on a WFS base URL and a version. The maximum
     * number of features to be handled/fetched by this store is limited by {WFS_MAX_FEATURES}.
     *
     * @param wfsUrl     The base URL of the WFS server to fetch the features from.
     * @param wfsVersion The WFS version to use.
     * @return The created {@link WFSDataStore}.
     * @throws GeoServerRESTImporterException
     */
    private WFSDataStore createWfsDataStore(String wfsUrl, String wfsVersion)
        throws GeoServerRESTImporterException {

        WFSDataStore wfsDataStore = null;

        try {
            WFSDataStoreFactory wfsFactory = new WFSDataStoreFactory();
            Map<String, Serializable> wfsDataStoreParams = new HashMap<String, Serializable>();

            List<BasicNameValuePair> wfsGetCapabilitiesQueryParams = new ArrayList<>();
            wfsGetCapabilitiesQueryParams.add(new BasicNameValuePair(
                "SERVICE", "WFS"));
            wfsGetCapabilitiesQueryParams.add(new BasicNameValuePair(
                "REQUEST", "GetCapabilities"));
            wfsGetCapabilitiesQueryParams.add(new BasicNameValuePair(
                "VERSION", wfsVersion));
            String wfsGetCapabilitiesQueryString = URLEncodedUtils.format(
                wfsGetCapabilitiesQueryParams, "UTF-8");
            String wfsGetCapabilitiesUrl = wfsUrl + "?" + wfsGetCapabilitiesQueryString;
            wfsDataStoreParams.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", wfsGetCapabilitiesUrl);
            wfsDataStoreParams.put("WFSDataStoreFactory:MAXFEATURES", WFS_MAX_FEATURES);

            log.debug("Creating a WFS dataStore based on the following WFS GetCapabilities: " +
                wfsGetCapabilitiesUrl);

            wfsDataStore = wfsFactory.createDataStore(wfsDataStoreParams);

            log.debug("Successfully created the WFS dataStore.");
        } catch (IOException e) {
            String errMsg = "Error while creating the WFS dataStore";
            log.error(errMsg + ": ", e);
            throw new GeoServerRESTImporterException(errMsg + ".");
        }

        return wfsDataStore;
    }

    /**
     * Fetches the features from the given featureType of the {@link DataStore} in the
     * provided EPSG.
     *
     * @param dataStore       The {@link DataStore} to fetch the features from.
     * @param featureTypeName The featureType to fetch.
     * @param targetEpsg      The EPSG of the returning collection.
     * @return The {@link SimpleFeatureCollection} including the features.
     * @throws GeoServerRESTImporterException
     */
    private SimpleFeatureCollection getFeatureCollectionFromDataStore(DataStore dataStore,
                                                                      String featureTypeName, String targetEpsg) throws GeoServerRESTImporterException {

        SimpleFeatureCollection featureCollection = null;
        ContentFeatureSource source = null;
        try {
            source = (ContentFeatureSource) dataStore.getFeatureSource(featureTypeName);
            featureCollection = source.getFeatures();

            log.debug("Successfully fetched " + featureCollection.size() + " features " +
                "from featureType " + featureTypeName);
        } catch (IOException e) {
            String errMsg = "Could not fetch the features from the provided dataStore";
            log.error(errMsg + ": ", e);
            throw new GeoServerRESTImporterException(errMsg + ".");
        }

        // Define the target CRS.
        CoordinateReferenceSystem targetCrs;
        try {
            targetCrs = CRS.decode(targetEpsg);
        } catch (FactoryException e) {
            String errMsg = "Could not decode the targetEpsg";
            log.error(errMsg + ": ", e);
            throw new GeoServerRESTImporterException(errMsg + ".");
        }

        // Transform the features to target CRS if necessary.
        CoordinateReferenceSystem sourceCrs = featureCollection.getSchema()
            .getGeometryDescriptor().getCoordinateReferenceSystem();
        if (!sourceCrs.equals(targetCrs)) {
            try {
                featureCollection = new ReprojectFeatureResults(
                    featureCollection,
                    targetCrs
                );
            } catch (NoSuchElementException | IOException | SchemaException |
                TransformException | FactoryException e) {
                String errMsg = "Could not reproject the feature collection";
                log.error(errMsg + ": ", e);
                throw new GeoServerRESTImporterException(errMsg + ".");
            }
        }

        return featureCollection;
    }

    /**
     * Imports a {@link SimpleFeatureCollection} into the designated import database.
     * The connection parameters for the connection will be acquired from the
     * targetDatastore in the GeoServer. Note: These parameters may be overridden
     * by targetDatastoreConnParams!
     *
     * @param featureCollection The featureCollection to import.
     * @return The name of the new table the features where imported in.
     * @throws GeoServerRESTImporterException
     */
    private String importFeatureCollectionToDatabase(SimpleFeatureCollection featureCollection)
        throws GeoServerRESTImporterException {

        // Read old feature schema.
        SimpleFeatureType schemaOrig = featureCollection.getSchema();
        String origSchemaName = schemaOrig.getTypeName();
        if (origSchemaName.contains(":")) {
            origSchemaName = origSchemaName.replace(":", "_");
        }

        // Create DB schema / table definition with the following table name.
        String schemaNameDB = WFS_IMPORT_TABLE_PREFIX + "_" + System.currentTimeMillis() + "_" + origSchemaName;
        if (!TableNameValidator.isValidName(schemaNameDB)) {
            schemaNameDB = TableNameValidator.createValidTableName(schemaNameDB);
        }
        schemaNameDB = schemaNameDB.toUpperCase();

        VectorFeatureTypeTransformer featureTypeTransform = new VectorFeatureTypeTransformer(
            schemaOrig, schemaNameDB, false);

        it.geosolutions.geoserver.rest.decoder.RESTDataStore restDataStore = this.geoServerManager.getReader().getDatastore(
            this.importerProperties.getTargetWorkspace(),
            this.importerProperties.getVector().getTargetDatastore()
        );
        Map<String, String> dbParameters = restDataStore.getConnectionParameters();
        dbParameters.putAll(this.importerProperties.getVector().getTargetDatastoreConnectionParams());

        String errMsg = "Could not write the features to database";
        JDBCDataStore jdbcDatastore = null;
        try {
            // Create JDBC datastore out of the given connection params.
            jdbcDatastore = (JDBCDataStore) DataStoreFinder.getDataStore(dbParameters);

            if (jdbcDatastore == null) {
                final String message = "Could not create jdbc datastore based on given dbParameters for WFS import. "
                    + "Are you sure that you have all necessary dependencies "
                    + "(like gt-jdbc-oracle or gt-jdbc-postgis)?";
                log.error(message);
                throw new Exception(message);
            }

            // Create DB schema.
            featureTypeTransform.createSchema();
            // Apply new schema to features.
            List<SimpleFeature> updatedFeatures = featureTypeTransform.createFeatureList(featureCollection);
            // Write features to database.
            boolean res = featureTypeTransform.toDataStore(jdbcDatastore, updatedFeatures);
            if (res) {
                log.info("Succesfully wrote the features to database in table " + schemaNameDB);
            } else {
                throw new GeoServerRESTImporterException(errMsg + ".");
            }
        } catch (Exception e) {
            log.error(errMsg + ": ", e);
            throw new GeoServerRESTImporterException(errMsg + ".");
        } finally {
            if (jdbcDatastore != null) {
                jdbcDatastore.dispose();
            }
        }

        return schemaNameDB;
    }

    /**
     * @param tableName
     * @param layerTitle
     */
    private boolean publishGeoServerLayerFromDbTable(String tableName, String layerTitle,
                                                     String targetEpsg) {
        GSFeatureTypeEncoder gsfte = new GSFeatureTypeEncoder();
        gsfte.setName(tableName.toUpperCase());
        gsfte.setTitle(layerTitle);
        gsfte.setNativeCRS(targetEpsg);
        gsfte.setSRS(targetEpsg);
        gsfte.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.NONE);

        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setEnabled(true);

        boolean success = this.geoServerManager.getPublisher().publishDBLayer(
            this.importerProperties.getTargetWorkspace(),
            this.importerProperties.getVector().getTargetDatastore(), gsfte, layerEncoder);

        return success;
    }

    /**
     * @param layerName
     * @param layerType
     * @param importJobId
     * @param taskId
     * @param fileProjection
     * @return
     * @throws Exception
     */
    public Map<String, Object> updateCrsForImport(String layerName, String layerType,
                                                  Integer importJobId, Integer taskId, String fileProjection)
        throws Exception {
        RESTLayer updateLayer = new RESTLayer();
        updateLayer.setSrs(fileProjection);

        this.geoServerImporter.updateImportTask(importJobId, taskId, updateLayer);

        RESTImportTaskList importTaskList = this.geoServerImporter.getRESTImportTasks(importJobId);
        createTransformTasks(fileProjection, layerType, importJobId, importTaskList);
        return this.runJobAndCreateLayer(layerName, layerType, importJobId, true);
    }

    /**
     * @param importJobId
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public Map<String, Object> deleteImportJob(Integer importJobId)
        throws URISyntaxException, HttpException {
        Map<String, Object> responseMap = new HashMap<>();

        if (this.geoServerImporter.deleteImportJob(importJobId)) {
            responseMap.put("success", true);
            responseMap.put("message", "Deleted ImportJob " + importJobId);
        } else {
            responseMap.put("success", false);
            responseMap.put("message", "Could not delete ImportJob " + importJobId);
        }

        return responseMap;
    }

    /**
     * @param importJobId
     * @throws Exception
     */
    public void deleteTemporaryShapeFiles(Integer importJobId) throws Exception {
        RESTData data = this.geoServerImporter.getDataOfImportTask(importJobId, 0);
        String restUrl = this.importerProperties.getGeoserver().getBaseUrl();
        restUrl += "/rest/resource/";

        // something like /var/lib/tomcat7/webapps/geoserver/data/uploads/tmp638865446314869143
        String fileUrl = data.getLocation();

        fileUrl = fileUrl.substring(fileUrl.lastIndexOf("uploads/"));
        restUrl += fileUrl;

        HttpResponse layerDeleted = HttpUtil.delete(restUrl,
            this.importerProperties.getGeoserver().getUsername(),
            this.importerProperties.getGeoserver().getPassword()
        );
        if (layerDeleted.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully deleted Layer data on the file system: " + fileUrl);
        } else {
            throw new Exception("Could not delete Layer data on the file system: " + fileUrl);
        }
    }

    /**
     *
     * @param featureTypeEncoder
     * @param workspace
     * @param dataStoreName
     * @param featureTypeName
     * @return
     */
    public boolean configureFeatureType(final GSFeatureTypeEncoder featureTypeEncoder,
                                        final String workspace, final String dataStoreName, final String featureTypeName) throws URISyntaxException, HttpException {

        if (featureTypeEncoder == null) {
            log.error("Unable to configure a featureType without a GSFeatureTypeEncoder.");
            return false;
        }

        if (StringUtils.isEmpty(workspace)) {
            log.error("Unable to configure a featureType without a workspace name.");
            return false;
        }

        if (StringUtils.isEmpty(dataStoreName)) {
            log.error("Unable to configure a featureType without a dataStoreName name.");
            return false;
        }

        if (StringUtils.isEmpty(featureTypeName)) {
            log.error("Unable to configure a featureType without a featureTypeName name.");
            return false;
        }

        GeoServerRESTReader reader = geoServerManager.getReader();

        // Check if the featureType is available.
        boolean existsFeatureType = reader.existsFeatureType(workspace, dataStoreName, featureTypeName);
        if (!existsFeatureType) {
            log.error("FeatureType does not exist in GeoServer!");
            return false;
        }

        final String url = this.importerProperties.getGeoserver().getBaseUrl() + "/rest/workspaces/" + workspace +
            "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName + ".xml";

        final String xmlBody = featureTypeEncoder.toString();
        final HttpResponse response = HttpUtil.put(
            url,
            xmlBody,
            ContentType.APPLICATION_XML,
            this.importerProperties.getGeoserver().getUsername(),
            this.importerProperties.getGeoserver().getPassword(),
            false
        );

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            log.error("Error configuring featureType " + workspace + ":" +
                dataStoreName + ":" + featureTypeName + " (" + new String(response.getBody()) + ")");
        } else {
            log.debug("FeatureType successfully configured " + workspace + ":" +
                dataStoreName + ": " + featureTypeName);
        }

        return response != null;
    }

}
