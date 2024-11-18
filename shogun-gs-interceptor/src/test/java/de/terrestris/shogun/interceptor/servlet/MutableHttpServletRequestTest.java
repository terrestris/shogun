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

package de.terrestris.shogun.interceptor.servlet;

import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.exception.InterceptorException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MutableHttpServletRequestTest {

    private final String featureTypeName = "TEST:MY_FEATURE_TYPE";
    private final String namespaceUrl = "http://localhost/TEST";

    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void extractWfsGetFeatureEndpointCorrectly() throws IOException, InterceptorException {
        String xmlData = """
            <?xml version="1.0" encoding="UTF-8"?>
            <GetFeature
                xmlns="http://www.opengis.net/wfs"
                service="WFS" version="1.1.0"
                outputFormat="application/json"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd"
            >
                <Query typeName="%s" xmlns:TEST="%s">
                    <Filter xmlns="http://www.opengis.net/ogc"><PropertyIsEqualTo><PropertyName>NUM</PropertyName><Literal>1909</Literal></PropertyIsEqualTo></Filter>
                </Query>
            </GetFeature>
            """.formatted(featureTypeName, namespaceUrl);

        InputStream xmlInputStream = new ByteArrayInputStream(xmlData.getBytes());
        when(request.getInputStream()).thenReturn(new MockServletInputStream(xmlInputStream));

        String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(request, OgcEnum.EndPoint.getAllValues());
        assertEquals(featureTypeName, requestEndPoint);
    }

    @Test
    void extractWfsTransactionUpdateEndpointCorrectly() throws IOException, InterceptorException {
        String xmlData = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Transaction
                xmlns="http://www.opengis.net/wfs"
                service="WFS" version="1.1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd"
            >
                <Update typeName="%s" xmlns:TEST="%s">
                    <Property>
                        <Name>GEOMETRIE</Name>
                        <Value>
                            <MultiPoint xmlns="http://www.opengis.net/gml" srsName="EPSG:25832"><pointMember><Point srsName="EPSG:25832"><pos srsDimension="2">373678.86587162 5606983.66695109</pos></Point></pointMember></MultiPoint>
                        </Value>
                    </Property>
                    <Property>
                        <Name>ID</Name><Value>1909</Value>
                    </Property>
                    <Filter xmlns="http://www.opengis.net/ogc"><FeatureId fid="TEST.19"/></Filter>
                </Update>
            </Transaction>
            """.formatted(featureTypeName, namespaceUrl);

        InputStream xmlInputStream = new ByteArrayInputStream(xmlData.getBytes());
        when(request.getInputStream()).thenReturn(new MockServletInputStream(xmlInputStream));

        String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(request, OgcEnum.EndPoint.getAllValues());
        assertEquals(featureTypeName, requestEndPoint);
    }

    @Test
    void extractWfsTransactionInsertEndpointCorrectly() throws IOException, InterceptorException {
        String xmlData = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Transaction
                xmlns="http://www.opengis.net/wfs"
                service="WFS"
                version="1.1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd">
                <Insert>
                    <%s xmlns:TEST="%s">
                        <TEST:NUM>1909</TEST:NUM>
                        <TEST:GEOMETRY>
                            <MultiPoint xmlns="http://www.opengis.net/gml" srsName="EPSG:25832">
                                <pointMember>
                                    <Point srsName="EPSG:25832">
                                        <pos srsDimension="2">373740.32587162 5606976.66695109</pos>
                                    </Point>
                                </pointMember>
                                <pointMember>
                                    <Point srsName="EPSG:25832">
                                        <pos srsDimension="2">373721.70587162 5606971.34695109</pos>
                                    </Point>
                                </pointMember>
                            </MultiPoint>
                        </TEST:GEOMETRY>
                    </%s>
                </Insert>
            </Transaction>
            """.formatted(featureTypeName, namespaceUrl, featureTypeName);

        InputStream xmlInputStream = new ByteArrayInputStream(xmlData.getBytes());
        when(request.getInputStream()).thenReturn(new MockServletInputStream(xmlInputStream));

        String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(request, OgcEnum.EndPoint.getAllValues());
        assertEquals(featureTypeName, requestEndPoint);
    }

    private static class MockServletInputStream extends jakarta.servlet.ServletInputStream {
        private final InputStream inputStream;

        public MockServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() {
            try {
                return inputStream.read();
            } catch (Exception e) {
                return -1;
            }
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {
        }
    }
}
