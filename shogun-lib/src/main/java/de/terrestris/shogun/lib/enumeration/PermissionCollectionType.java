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
package de.terrestris.shogun.lib.enumeration;

public enum PermissionCollectionType {
    CREATE("CREATE"),
    READ("READ"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),

    CREATE_READ("CREATE_READ"),
    CREATE_UPDATE("CREATE_UPDATE"),
    CREATE_DELETE("CREATE_DELETE"),
    READ_UPDATE("READ_UPDATE"),
    READ_DELETE("READ_DELETE"),
    UPDATE_DELETE("UPDATE_DELETE"),

    CREATE_READ_UPDATE("CREATE_READ_UPDATE"),
    CREATE_READ_DELETE("CREATE_READ_DELETE"),
    CREATE_UPDATE_DELETE("CREATE_UPDATE_DELETE"),
    READ_UPDATE_DELETE("READ_UPDATE_DELETE"),

    ADMIN("ADMIN");

    private final String type;

    private PermissionCollectionType(String type) {
        this.type = type;
    }
}
