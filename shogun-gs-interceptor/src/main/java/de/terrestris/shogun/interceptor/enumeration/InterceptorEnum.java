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
package de.terrestris.shogun.interceptor.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class InterceptorEnum {

    /**
     * A enum type for the allowed rule format.
     */
    public enum RuleType {
        ALLOW("ALLOW"),
        DENY("DENY"),
        MODIFY("MODIFY");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        RuleType(String value) {
            this.value = value;
        }

        /**
         * Static method to get an enum based on a string value.
         * This method is annotated with {@link JsonCreator},
         * which allows the client to send case insensitive string
         * values (like "jSon"), which will be converted to the
         * correct enum value.
         *
         * @param inputValue
         * @return
         */
        @JsonCreator
        public static RuleType fromString(String inputValue) {
            if (inputValue != null) {
                for (RuleType type : RuleType.values()) {
                    if (inputValue.equalsIgnoreCase(type.value)) {
                        return type;
                    }
                }
            }
            return null;
        }

        /**
         * This method is annotated with {@link JsonValue},
         * so that jackson will serialize the enum value to
         * the (lowercase) {@link #value}.
         */
        @Override
        @JsonValue
        public String toString() {
            return value;
        }
    }

}
