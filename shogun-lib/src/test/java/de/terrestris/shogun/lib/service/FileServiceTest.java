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
package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.properties.FileUploadProperties;
import de.terrestris.shogun.properties.UploadProperties;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Mock
    private UploadProperties uploadProperties;

    @InjectMocks
    private FileService fileService;

    private String failMsg = "Validation shouldn't have thrown any exception.";

    public void initializeConfig(List<String> supportedContentTypes) {
        FileUploadProperties fileUploadProperties = new FileUploadProperties();
        fileUploadProperties.setSupportedContentTypes(supportedContentTypes);

        when(uploadProperties.getFile()).thenReturn(fileUploadProperties);
    }

    @Test
    public void isValidType_failsIfNoConfigIsGiven() {
        // initializeConfig hasn't been called.
        assertThrows(InvalidContentTypeException.class, () -> fileService.isValidType("application/zip"));
    }

    @Test
    public void isValidType_shouldAllowGivenContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("application/pdf");
        supportedContentTypes.add("application/json");

        initializeConfig(supportedContentTypes);

        // Allow the explicitly given ones.
        try {
            fileService.isValidType("application/pdf");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            fileService.isValidType("application/json");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }
    }

    @Test
    public void isValidType_shouldDenyGivenContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("application/pdf");
        supportedContentTypes.add("application/json");

        initializeConfig(supportedContentTypes);

        // Deny others than the explicitly given ones.
        assertThrows(InvalidContentTypeException.class, () -> fileService.isValidType("application/zip"));
    }

    @Test
    public void isValidType_shouldAllowAllUnspecificContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("application/*");

        initializeConfig(supportedContentTypes);

        // Allow application/*.
        try {
            fileService.isValidType("application/pdf");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            fileService.isValidType("application/json");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            fileService.isValidType("application/zip");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        // Deny others than application/*.
        assertThrows(InvalidContentTypeException.class, () -> fileService.isValidType("image/png"));
    }

    @Test
    public void isValidType_shouldAllowAllContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("*/*");

        initializeConfig(supportedContentTypes);

        // Allow */*.
        try {
            fileService.isValidType("application/zip");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            fileService.isValidType("this/is");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            fileService.isValidType("no/contenttype");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }
    }

}
