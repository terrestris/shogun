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
package de.terrestris.shogun.lib.util;

import de.terrestris.shogun.lib.model.BaseEntity;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.apache.logging.log4j.LogManager.getLogger;

public class IdHelper {

    private static final Logger logger = getLogger(IdHelper.class);

    /**
     * Helper method that uses reflection to set the (inaccessible) id field of
     * the given {@link BaseEntity}.
     *
     * @param baseEntity The entity with the inaccessible id field
     * @param id The id to set
     * @throws NoSuchFieldException no such field exception
     */
    public static void setIdForEntity(BaseEntity baseEntity, Long id) throws NoSuchFieldException {
        // use reflection to get the inaccessible final field 'id'
        Field idField = BaseEntity.class.getDeclaredField("id");

        AccessController.doPrivileged((PrivilegedAction<BaseEntity>) () -> {
            idField.setAccessible(true);
            try {
                idField.set(baseEntity, id);
            } catch (IllegalAccessException e) {
                logger.error("Could not set ID field for persistent object", e);
            }
            idField.setAccessible(false);
            return null;
        });
    }

}
