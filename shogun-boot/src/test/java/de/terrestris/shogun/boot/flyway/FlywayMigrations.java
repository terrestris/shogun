package de.terrestris.shogun.boot.flyway;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

import java.io.File;
import org.junit.Test;

public class FlywayMigrations {

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
