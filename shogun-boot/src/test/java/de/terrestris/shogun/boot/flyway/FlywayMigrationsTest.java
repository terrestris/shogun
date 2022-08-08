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
package de.terrestris.shogun.boot.flyway;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

public class FlywayMigrationsTest {

    @Test
    public void validateFilenames() {
        File migrationFolder = new File("src/main/resources/db/migration");
        File[] migrationFiles = migrationFolder.listFiles();

        for (File migrationFile : migrationFiles) {
            if (migrationFile.isFile()) {
                String migrationFilename = migrationFile.getName();

                assertThat(
                    migrationFilename,
                    matchesPattern("^V0\\.\\d+\\.\\d+(\\_\\_)[^_][\\w]+[^_]\\.(sql|java|sh)$")
                );
            }
        }
    }

}
