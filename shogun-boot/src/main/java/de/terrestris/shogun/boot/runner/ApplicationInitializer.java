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
package de.terrestris.shogun.boot.runner;

import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Component
@Log4j2
public class ApplicationInitializer implements ApplicationRunner {

    @Autowired
    protected PermissionCollectionRepository permissionCollectionRepository;

    public void run(ApplicationArguments args) {
        checkExistenceOfPermissions();
    }

    /**
     * Checks if the permissions are available. The permissions are required to set the domain based access control and
     * should be imported through the existing flyway migrations.
     */
    private void checkExistenceOfPermissions() {
        Iterable<PermissionCollection> permissions = permissionCollectionRepository.findAll();

        long permissionsLength = StreamSupport.stream(permissions.spliterator(), false).count();

        if (permissionsLength == 0) {
            log.fatal("Couldn't find any permissions in the database. This will most likely lead to errors. Please " +
                "ensure flyway is enabled in your application.yaml! Alternatively the permissions may be inserted " +
                "manually - even if not recommended!");
        }
    }
}
