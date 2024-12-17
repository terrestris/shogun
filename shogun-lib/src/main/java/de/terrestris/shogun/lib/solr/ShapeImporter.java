/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.solr;

import lombok.extern.log4j.Log4j2;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.WKTWriter2;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Import a shape file's fields into solr. Usage:
 * <code>
 *     ShapeImporter importer = new ShapeImporter("http://shogun-solr:8983/solr/search/", "myshape.shp", "myshape", "municipalities");
 *     importer.importShape();
 * </code>
 * This will import the features from the shape file into solr setting the special field 'category' to 'municipalities'
 * and using 'myshape_' as a prefix for the ids. The solr core 'search' will be used.
 */
@Log4j2
public class ShapeImporter {

    private final String solrUrl;
    private final String file;
    private final String prefix;
    private final String category;

    public ShapeImporter(String solrUrl, String file, String prefix, String category) {
        this.solrUrl = solrUrl;
        this.file = file;
        this.prefix = prefix;
        this.category = category;
    }

    private SolrInputDocument convertFromFeature(SimpleFeature feature) {
        SolrInputDocument doc = new SolrInputDocument();
        WKTWriter2 wkt = new WKTWriter2();
        doc.addField("id", String.format("%s_%s", prefix, feature.getID()));
        doc.addField("category", category);
        doc.addField("title", feature.getProperty("EBENE").getValue() + " " + feature.getProperty("NAME").getValue());
        for (Property prop : feature.getProperties()) {
            if (prop.getName().getLocalPart().equals("the_geom")) {
                continue;
            }
            doc.addField("search", prop.getValue());
            doc.addField(prop.getName().getLocalPart(), prop.getValue());
        }
        Geometry geom = (Geometry) feature.getDefaultGeometry();
        GeometryFactory factory = new GeometryFactory();
        if (geom instanceof Polygon && !geom.isValid()) {
            List<Polygon> polygons = JTS.makeValid((Polygon) geom, false);
            MultiPolygon multi = factory.createMultiPolygon(polygons.toArray(new Polygon[0]));
            doc.addField("geometry", wkt.write(multi));
        } else if (geom instanceof MultiPolygon && !geom.isValid()) {
            List<Polygon> polygons = new ArrayList<>();
            MultiPolygon multi = (MultiPolygon) geom;
            for (int i = 0; i < multi.getNumGeometries(); ++i) {
                polygons.addAll(JTS.makeValid((Polygon) multi.getGeometryN(i), false));
            }
            MultiPolygon newMulti = factory.createMultiPolygon(polygons.toArray(new Polygon[0]));
            doc.addField("geometry", wkt.write(newMulti));
        } else {
            doc.addField("geometry", wkt.write(geom));
        }
        return doc;
    }

    public void importShape() throws IOException, SolrServerException, FactoryException {
        log.info("Importing from {}", file);
        File shp = new File(file);
        Map<String, Object> map = new HashMap<>();
        map.put("url", shp.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE;

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = new ReprojectingFeatureCollection(source.getFeatures(filter),  CRS.decode("CRS:84"));
        try (Http2SolrClient solr = new Http2SolrClient.Builder(solrUrl).build();
             FeatureIterator<SimpleFeature> features = collection.features()) {
            solr.setParser(new XMLResponseParser());
            solr.deleteByQuery("id:" + prefix + "*");
            while (features.hasNext()) {
                try {
                    solr.add(convertFromFeature(features.next()));
                } catch (Exception e) {
                    log.error("Error importing document: {}", e.getMessage());
                    log.trace("Stack trace:", e);
                }
            }
            solr.commit();
        }
        log.info("Done importing.");
    }

}
