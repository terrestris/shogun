package de.terrestris.shogun.importer.transformer;

import lombok.extern.log4j.Log4j2;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for transformation of shape features into features of a JDBC-data store
 *
 * @author Andre Henn
 */
@Log4j2
public class VectorFeatureTypeTransformer {

    public static int DEFAULT_SRID = -1;
    public static CoordinateReferenceSystem DEFAULT_CRS;

    private SimpleFeatureType sft_original;
    private SimpleFeatureType sft_database;
    private String tNameNew;
    private HashMap<String, String> hashMapAttributeNamesOrig_After;
    private HashMap<String, String> hashMapAttributeNamesDatatypes;

    private boolean resetSchema;
    private int successfullyImported;

    /**
     * Constructor
     *
     * @param sft_old   SimpleFeatureType of original data (e.g. shape file)
     * @param schemaNew name of new schema (e.g. table name in db)
     * @param reset     Should new data be appended to existing table (yes / no)
     */
    public VectorFeatureTypeTransformer(SimpleFeatureType sft_old, String schemaNew, boolean reset) {
        this.sft_original = sft_old;
        this.tNameNew = schemaNew;
        this.hashMapAttributeNamesOrig_After = new HashMap<String, String>();
        this.hashMapAttributeNamesDatatypes = new HashMap<String, String>();
        this.resetSchema = reset;
        this.successfullyImported = 0;

        assert (DEFAULT_CRS != null);
    }

    /**
     * Method to create the SimpleFeatureType
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.geotools.feature.SchemaException
     * @throws org.opengis.referencing.FactoryException
     */
    public void createSchema() throws NoSuchAuthorityCodeException, SchemaException, FactoryException {
        createSchema(this.tNameNew);
    }

    /**
     * Method to create the SimpleFeatureType
     *
     * @param nameNew name of the new SimpleFeatureType
     * @throws org.geotools.feature.SchemaException
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.referencing.FactoryException
     */
    public void createSchema(String nameNew) throws SchemaException, NoSuchAuthorityCodeException, FactoryException {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        CoordinateReferenceSystem crs = this.sft_original.getCoordinateReferenceSystem();

        if (crs == null) {
            crs = DEFAULT_CRS;
            log.warn("DEFAULT CRS " + DEFAULT_CRS + " used for import!");
        }

        builder.setCRS(crs);
        builder.setName(nameNew);

        List<AttributeType> typen = this.sft_original.getTypes();
        for (AttributeType at : typen) {
            if (at instanceof GeometryTypeImpl) {
                GeometryTypeImpl geom = (GeometryTypeImpl) at;
                Class<?> binding = geom.getBinding();
                String oldName = geom.getName().getLocalPart();
                /* The shape file format has a couple limitations:
                 * - "THE_GEOM" is always first, and used for the geometry attribute name
                 * - "THE_GEOM" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
                 * - attribute name of geometry column has to be uppercase
                 * - Attribute names are limited in length
                 * - Not all data types are supported (example timestamp represented as Date)
                 */
                String mappedName = "THE_GEOM";
                builder.setCRS(crs);
                builder.add(mappedName, binding);
                String simpleName = binding.getSimpleName();

                // Add geometry.
                this.hashMapAttributeNamesOrig_After.put(oldName, mappedName);
                this.hashMapAttributeNamesDatatypes.put(mappedName, simpleName.toUpperCase());

            } else if (at instanceof AttributeTypeImpl) {
                AttributeTypeImpl atImpl = (AttributeTypeImpl) at;
                String oldName = atImpl.getName().getLocalPart();
                Class<?> binding = atImpl.getBinding();
                builder.add(oldName, binding);
                String simpleName = binding.getSimpleName();

                this.hashMapAttributeNamesOrig_After.put(oldName, oldName);
                this.hashMapAttributeNamesDatatypes.put(oldName, simpleName.toUpperCase());
            }
        }

        final SimpleFeatureType dbSchemaNew = builder.buildFeatureType();
        this.sft_database = dbSchemaNew;
    }

    /**
     * Create list of {@link org.opengis.feature.simple.SimpleFeature} based on a given {@link org.geotools.data.simple.SimpleFeatureCollection}
     * - for geometries that are not <code>null</code>
     * - for new before constructed feature type
     *
     * @param featureCollection {@link org.geotools.feature.FeatureCollection} to be transformed
     * @return {@link org.opengis.feature.simple.SimpleFeature} list
     * @throws java.io.IOException
     * @throws org.opengis.referencing.FactoryException
     */
    public List<SimpleFeature> createFeatureList(SimpleFeatureCollection featureCollection) throws IOException, FactoryException {
        ArrayList<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
        SimpleFeatureIterator sfi = featureCollection.features();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(this.sft_database);

        int sridDecoded = -1;
        while (sfi.hasNext()) {
            SimpleFeature sd = sfi.next();
            if (sd == null) {
                log.warn("SimpleFeature is null => ignored");
                continue;
            }
            String id = sd.getID();
            for (String oldKey : this.hashMapAttributeNamesOrig_After.keySet()) {
                String newKey = this.hashMapAttributeNamesOrig_After.get(oldKey);
                if (!newKey.equalsIgnoreCase("the_geom")) {
                    Object objCurrent = sd.getAttribute(oldKey);
                    if (objCurrent != null) {
                        featureBuilder.set(newKey, objCurrent);
                    } else {
                        log.warn("Attribute (old key: " + oldKey + ") null / not found => ignored");
                        continue;
                    }
                } else {
                    Geometry gOld = (Geometry) sd.getDefaultGeometry();
                    if (gOld == null) {
                        log.warn("Geometry of feature " + sd + " is null / not found => feature ignored");
                        continue;
                    }

                    // Check if SRID is set
                    if (gOld.getSRID() == 0) {
                        if (sridDecoded == -1) {
                            Integer srid = CRS.lookupEpsgCode(this.sft_original
                                .getCoordinateReferenceSystem(), true);
                            if (srid != null) {
                                sridDecoded = srid.intValue();
                            } else {
                                assert (DEFAULT_SRID > 0);
                                sridDecoded = DEFAULT_SRID;
                            }
                        }
                        gOld.setSRID(sridDecoded);
                    }

                    featureBuilder.set(newKey, gOld);
                }
            }
            SimpleFeature featureNew = featureBuilder.buildFeature(id);
            featureList.add(featureNew);

        }
        sfi.close();
        return featureList;
    }

    /**
     * Save {@link org.opengis.feature.simple.SimpleFeature} list in database (or another {@link org.geotools.data.DataStore})
     *
     * @param store           (JDBC)-Datastore
     * @param featuresToWrite list of features
     * @return <code>true</code> if insert was successful, <code>false</code> otherwise
     * @throws java.io.IOException
     */
    public boolean toDataStore(DataStore store, List<SimpleFeature> featuresToWrite) throws IOException {

        String typename = this.sft_database.getTypeName();
        // Try to reset the schema.
        if (this.resetSchema) {
            try {
                log.info("Reset schema");
                store.removeSchema(typename);
                log.info("done.");
            } catch (IllegalArgumentException iae) {
                log.warn("Table: " + this.sft_database.getName() + " does not exist "
                    + "in the database; no reset of the feature.");
            }
        }

        try {
            log.info("Create schema");
            store.createSchema(this.sft_database);
        } catch (IOException e) {
            log.error("Error while creating the shema: ", e);

            // TODO add option, if table should be created with another name
            return false;
        }

        String typeName = typename.toUpperCase(); // oracle
        SimpleFeatureSource featureSource = store.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length
         * - Not all data types are supported (example Timestamp represented as Date)
         *
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */

        log.info("Write SHAPE: " + SHAPE_TYPE);

        if (store instanceof JDBCDataStore) {

            JDBCDataStore jdbcDS = (JDBCDataStore) store;
            Transaction transaction = new DefaultTransaction("Add features");

            FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriterDB =
                jdbcDS.getFeatureWriterAppend(typeName, transaction);

            try {
                for (SimpleFeature feature : featuresToWrite) {
                    SimpleFeature copy = featureWriterDB.next();
                    copy.setAttributes(feature.getAttributes());

                    Geometry geometry2 = (Geometry) feature.getDefaultGeometry();
                    copy.setDefaultGeometry(geometry2);

                    featureWriterDB.write();
                    this.successfullyImported++;
                }

                transaction.commit();

                log.info("Data of " + typename + " succesfully written to database.");
            } catch (Exception e) {
                log.error("Error while writing to the database: " + e);
                transaction.rollback();
            } finally {
                featureWriterDB.close();
                transaction.close();
            }

            return true;
        } else {
            log.error(typename + " does not support read/write access.");
        }

        return false;
    }

    /**
     * <p>Getter for the field <code>hashMapAttributeNamesDatatypes</code>.</p>
     *
     * @return the hashMapAttributeNamesDatatypes
     */
    public HashMap<String, String> getHashMapAttributeNamesDatatypes() {
        return hashMapAttributeNamesDatatypes;
    }

    /**
     * <p>getNumberOfSuccessfullyImportedRecords.</p>
     *
     * @return number of successfully imported records to database / {@link org.geotools.data.DataStore}
     */
    public int getNumberOfSuccessfullyImportedRecords() {
        return this.successfullyImported;
    }

}
