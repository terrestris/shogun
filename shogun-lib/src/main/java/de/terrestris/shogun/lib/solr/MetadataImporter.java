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
import org.apache.http.client.utils.URIBuilder;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Import the metadata docs from a given CSW into solr. Usage:
 * <code>
 *     MetadataImporter importer = new MetadataImporter("http://shogun-solr:8983/solr/search/", "https://some-catalog.com/geonetwork/srv/ger/csw", "some-catalog", "metadata");
 *     importer.importDocumentsFromCsw();
 * </code>
 * This will import the metadata documents from the CSW into solr setting the special field 'category' to 'metadata'
 * and using 'some-catalog_' as a prefix for the ids. The solr core 'search' will be used. Not the whole CSW will be
 * queried at once, the import will request 100 documents at a time and page through the CSW.
 */
@Log4j2
public class MetadataImporter {

    private final String solrUrl;
    private final String baseUrl;
    private final String prefix;
    private final String category;

    public MetadataImporter(String solrUrl, String baseUrl, String prefix, String category) {
        this.solrUrl = solrUrl;
        this.baseUrl = baseUrl;
        this.prefix = prefix;
        this.category = category;
    }

    private String getText(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("CharacterString")) {
                return reader.getElementText();
            }
        }
        return null;
    }

    private double parseDecimal(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("Decimal")) {
                return Double.parseDouble(reader.getElementText());
            }
        }
        return 0;
    }

    private void parseTitle(XMLStreamReader reader, SolrInputDocument document) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isEndElement() && reader.getLocalName().equals("citation")) {
                return;
            }
            if (reader.isStartElement() && reader.getLocalName().equals("title")) {
                String text = getText(reader);
                document.addField("search", text);
                document.addField("title", text);
            }
        }
    }

    private void parseHierarchyLevel(XMLStreamReader reader, SolrInputDocument document) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("MD_ScopeCode")) {
                String hierarchyLevel = reader.getAttributeValue(null, "codeListValue");
                document.addField("hierarchyLevel", hierarchyLevel);
                document.addField("search", hierarchyLevel);
            }
            if (reader.isEndElement() && reader.getLocalName().equals("MD_ScopeCode")) {
                return;
            }
        }
    }

    private void parseServiceType(XMLStreamReader reader, SolrInputDocument document) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("LocalName")) {
                String serviceType = reader.getElementText();
                document.addField("serviceType", serviceType);
            }
            if (reader.isEndElement() && reader.getLocalName().equals("LocalName")) {
                return;
            }
        }
    }

    private void parseOperations(XMLStreamReader reader, SolrInputDocument document) throws XMLStreamException {
        boolean inOperationMetadata = false;
        boolean inConnectPoint = false;
        boolean inOnlineResource = false;
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("SV_OperationMetadata")) {
                inOperationMetadata = true;
            }

            if (inOperationMetadata && reader.isStartElement() && reader.getLocalName().equals("connectPoint")) {
                inConnectPoint = true;
            }
            if (reader.isEndElement() && reader.getLocalName().equals("connectPoint")) {
                inConnectPoint = false;
            }

            if (inConnectPoint && reader.isStartElement() && reader.getLocalName().equals("CI_OnlineResource")) {
                inOnlineResource = true;
            }
            if (reader.isEndElement() && reader.getLocalName().equals("CI_OnlineResource")) {
                inOnlineResource = false;
            }

            if (inOnlineResource && reader.isStartElement() && reader.getLocalName().equals("URL")) {
                document.addField("serviceUrl", reader.getElementText());
            }

            if (reader.isEndElement() && reader.getLocalName().equals("SV_OperationMetadata")) {
                return;
            }
        }
    }

    private SolrInputDocument convertMetadataDocument(XMLStreamReader reader) throws XMLStreamException {
        GeometryFactory factory = new GeometryFactory();
        WKTWriter2 wkt = new WKTWriter2();
        SolrInputDocument document = new SolrInputDocument();
        document.addField("baseurl", baseUrl);
        document.addField("category", category);
        double minx = 0, miny = 0, maxx = 0, maxy = 0;
        while (reader.hasNext()) {
            reader.next();
            if (reader.isEndElement() && reader.getLocalName().equals("MD_Metadata")) {
                break;
            }
            if (reader.isStartElement()) {
                switch (reader.getLocalName()) {
                    case "fileIdentifier":
                        document.addField("id", String.format("%s_%s", prefix, getText(reader)));
                        break;
                    case "hierarchyLevel":
                        parseHierarchyLevel(reader, document);
                        break;
                    case "hierarchyLevelName": {
                        String text = getText(reader);
                        document.addField("hierarchyLevelName", text);
                        document.addField("search", text);
                        break;
                    }
                    case "citation":
                        parseTitle(reader, document);
                        break;
                    case "abstract": {
                        String text = getText(reader);
                        document.addField("search", text);
                        document.addField("abstract", text);
                        break;
                    }
                    case "keyword": {
                        String text = getText(reader);
                        document.addField("search", text);
                        document.addField("keywords", text);
                        break;
                    }
                    case "serviceType":
                        parseServiceType(reader, document);
                        break;
                    case "containsOperations": {
                        String hierarchyLevel = (String) document.getFieldValue("hierarchyLevel");
                        if (hierarchyLevel == null || !hierarchyLevel.equals("service")) {
                            break;
                        }
                        parseOperations(reader, document);
                        break;
                    }
                    case "westBoundLongitude":
                        minx = parseDecimal(reader);
                        break;
                    case "eastBoundLongitude":
                        maxx = parseDecimal(reader);
                        break;
                    case "northBoundLatitude":
                        maxy = parseDecimal(reader);
                        break;
                    case "southBoundLatitude":
                        miny = parseDecimal(reader);
                        break;
                    default:
                        break;
                }
            }
        }
        Polygon polygon = factory.createPolygon(new Coordinate[]{
            new Coordinate(minx, miny),
            new Coordinate(maxx, miny),
            new Coordinate(maxx, maxy),
            new Coordinate(minx, maxy),
            new Coordinate(minx, miny)
        });
        document.addField("geometry", wkt.write(polygon));
        return document;
    }

    public void importDocumentsFromCsw() throws Exception {
        log.info("Importing from {}", baseUrl);
        HttpClient client = HttpClient.newHttpClient();
        URIBuilder builder = new URIBuilder(baseUrl);
        builder.addParameter("version", "2.0.2")
            .addParameter("service", "CSW")
            .addParameter("request", "GetRecords")
            .addParameter("elementSetName", "full")
            .addParameter("typeNames", "gmd:MD_Metadata")
            .addParameter("outputSchema", "http://www.isotc211.org/2005/gmd")
            .addParameter("resultType", "results")
            .addParameter("maxRecords", "100")
            .addParameter("startPosition", "1");
        int startPosition = 1;
        XMLInputFactory factory = XMLInputFactory.newFactory();
        try (Http2SolrClient solr = new Http2SolrClient.Builder(solrUrl).build()) {
            solr.setParser(new XMLResponseParser());
            solr.deleteByQuery("id:" + prefix + "*");
            while (startPosition != 0) {
                builder.setParameter("startPosition", String.format("%s", startPosition));
                HttpRequest get = HttpRequest.newBuilder(builder.build()).build();
                HttpResponse<InputStream> response = client.send(get, HttpResponse.BodyHandlers.ofInputStream());
                XMLStreamReader reader = factory.createXMLStreamReader(response.body());
                while (reader.hasNext()) {
                    reader.next();
                    if (reader.isStartElement() && reader.getLocalName().equals("SearchResults")) {
                        startPosition = Integer.parseInt(reader.getAttributeValue(null, "nextRecord"));
                    }
                    if (reader.isStartElement() && reader.getLocalName().equals("MD_Metadata")) {
                        SolrInputDocument document = convertMetadataDocument(reader);
                        try {
                            solr.add(document);
                        } catch (Exception e) {
                            log.warn("Unable to import solr document for {}: {}", document.get("id"), e.getMessage());
                            log.trace("Stack trace:", e);
                        }
                    }
                }
                if ((startPosition - 1) % 1000 == 0) {
                    log.info("Will fetch from start index {}, committing.", startPosition);
                    try {
                        solr.commit();
                    } catch (Exception e) {
                        log.warn("Unable to commit documents: {}", e.getMessage());
                        log.trace("Stack trace:", e);
                    }
                    log.info("Committed.");
                }
            }
            solr.commit();
        }
        log.info("Done importing.");
    }

}
