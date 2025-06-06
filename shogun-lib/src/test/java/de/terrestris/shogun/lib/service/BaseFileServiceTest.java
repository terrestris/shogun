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

import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

@ExtendWith(MockitoExtension.class)
public class BaseFileServiceTest {

    @InjectMocks
    private FileService fileService;

    private String failMsg = "Validation shouldn't have thrown any exception.";

    @Test
    public void isValidFileName_shouldAllowRegularFilenames() {
        try {
            fileService.isValidFileName("Peter.pdf");
            fileService.isValidFileName("Peter.png");
            fileService.isValidFileName("Mein Spezial Bild mit Leerzeichen.png");
            fileService.isValidFileName("Mein_Spezial_Bild_mit_Unterstrichen.png");
            fileService.isValidFileName("Mein-Spezial-Bild-mit-Bindestrichen.png");
            fileService.isValidFileName("@klammeraffe#hastag.png");
            fileService.isValidFileName("lord-fauntleroy.png");
        } catch(InvalidFileNameException e) {
            fail(failMsg);
        }
    }

    @Test
    public void isValidFileName_throwsIfFileNameIsIllegal() {
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter\\.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter/.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter:.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter*.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter?.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter\".pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter<.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter>.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter|.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter\\0.pdf"));
        assertThrows(InvalidFileNameException.class, () -> fileService.isValidFileName("Peter\\n.pdf"));
    }

}
