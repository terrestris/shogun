package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.properties.ImageFileUploadProperties;
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
        assertThrows(InvalidContentTypeException.class, () -> imageFileService.isValidType("image/png"));
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
