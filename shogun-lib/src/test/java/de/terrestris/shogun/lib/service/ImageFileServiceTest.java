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

import de.terrestris.shogun.properties.ImageFileUploadProperties;
import de.terrestris.shogun.properties.UploadProperties;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageFileServiceTest {

    @Mock
    private UploadProperties uploadProperties;

    @InjectMocks
    private ImageFileService imageFileService;

    private String failMsg = "Validation shouldn't have thrown any exception.";

    public void initializeConfig(List<String> supportedContentTypes) {
        ImageFileUploadProperties imageFileUploadProperties = new ImageFileUploadProperties();
        imageFileUploadProperties.setSupportedContentTypes(supportedContentTypes);

        when(uploadProperties.getImage()).thenReturn(imageFileUploadProperties);
    }

    @Test
    public void isValidType_failsIfNoConfigIsGiven() {
        // initializeConfig hasn't been called.
        assertThrows(NoSuchBeanDefinitionException.class, () -> imageFileService.isValidType("image/png"));
    }

    @Test
    public void isValidType_shouldAllowGivenContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("image/png");
        supportedContentTypes.add("image/jpg");

        initializeConfig(supportedContentTypes);

        // Allow the explicitly given ones.
        try {
            imageFileService.isValidType("image/png");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            imageFileService.isValidType("image/jpg");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }
    }

    @Test
    public void isValidType_shouldDenyGivenContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("image/png");
        supportedContentTypes.add("image/jpg");

        initializeConfig(supportedContentTypes);

        // Deny others than the explicitly given ones.
        assertThrows(InvalidContentTypeException.class, () -> imageFileService.isValidType("image/jpeg"));
    }

    @Test
    public void isValidType_shouldAllowAllUnspecificContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("image/*");

        initializeConfig(supportedContentTypes);

        // Allow image/*.
        try {
            imageFileService.isValidType("image/png");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            imageFileService.isValidType("image/jpg");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            imageFileService.isValidType("image/jpeg");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        // Deny others than image/*.
        assertThrows(InvalidContentTypeException.class, () -> imageFileService.isValidType("application/zip"));
    }

    @Test
    public void isValidType_shouldAllowAllContentTypes() {
        List<String> supportedContentTypes = new ArrayList<>();
        supportedContentTypes.add("*/*");

        initializeConfig(supportedContentTypes);

        // Allow */*.
        try {
            imageFileService.isValidType("image/png");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            imageFileService.isValidType("this/is");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }

        try {
            imageFileService.isValidType("no/contenttype");
        } catch(InvalidContentTypeException e) {
            fail(failMsg);
        }
    }

}
