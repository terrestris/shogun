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

import java.util.*;

public class OgcEnum {

    /**
     * A map that contains a set of {@link OperationType}s for any possible
     * {@link ServiceType}. See also the opposite collection
     * SERVICETYPES_BY_OPERATION.
     */

    public static final Map<ServiceType, Set<OperationType>> OPERATIONS_BY_SERVICETYPE;

    /**
     * A map that contains a set of {@link ServiceType}s for any possible
     * {@link OperationType}. See also the opposite collection
     * OPERATIONS_BY_SERVICETYPE.
     */
    public static final Map<OperationType, Set<ServiceType>> SERVICETYPES_BY_OPERATION;

    static {
        Map<ServiceType, Set<OperationType>> map = new HashMap<>();

        Set<OperationType> wmsOps = new HashSet<>();
        wmsOps.add(OperationType.GET_CAPABILITIES);
        wmsOps.add(OperationType.GET_MAP);
        wmsOps.add(OperationType.GET_FEATURE_INFO);
        wmsOps.add(OperationType.DESCRIBE_LAYER);
        wmsOps.add(OperationType.GET_LEGEND_GRAPHIC);
        wmsOps.add(OperationType.GET_STYLES);

        Set<OperationType> wfsOps = new HashSet<>();
        wfsOps.add(OperationType.GET_CAPABILITIES);
        wfsOps.add(OperationType.DESCRIBE_FEATURE_TYPE);
        wfsOps.add(OperationType.GET_FEATURE);
        wfsOps.add(OperationType.LOCK_FEATURE);
        wfsOps.add(OperationType.TRANSACTION);

        Set<OperationType> wcsOps = new HashSet<>();
        wcsOps.add(OperationType.GET_CAPABILITIES);
        wcsOps.add(OperationType.DESCRIBE_COVERAGE);
        wcsOps.add(OperationType.GET_COVERAGE);

        Set<OperationType> wpsOps = new HashSet<>();
        wpsOps.add(OperationType.GET_CAPABILITIES);
        wpsOps.add(OperationType.EXECUTE);
        wpsOps.add(OperationType.DESCRIBE_PROCESS);

        Set<OperationType> w3dsOps = new HashSet<>();
        w3dsOps.add(OperationType.GET_CAPABILITIES);
        w3dsOps.add(OperationType.GET_SCENE);
        w3dsOps.add(OperationType.GET_FEATURE_INFO);
        w3dsOps.add(OperationType.GET_LAYER_INFO);
        w3dsOps.add(OperationType.GET_TILE);

        Set<OperationType> wmtsOps = new HashSet<>();
        wmtsOps.add(OperationType.GET_CAPABILITIES);
        wmtsOps.add(OperationType.GET_TILE);
        wmtsOps.add(OperationType.GET_FEATURE_INFO);

        map.put(ServiceType.WMS, Collections.unmodifiableSet(wmsOps));
        map.put(ServiceType.WFS, Collections.unmodifiableSet(wfsOps));
        map.put(ServiceType.WCS, Collections.unmodifiableSet(wcsOps));
        map.put(ServiceType.WPS, Collections.unmodifiableSet(wpsOps));
        map.put(ServiceType.W3DS, Collections.unmodifiableSet(w3dsOps));
        map.put(ServiceType.WMTS, Collections.unmodifiableSet(wmtsOps));

        // store it in the lookup
        OPERATIONS_BY_SERVICETYPE = Collections.unmodifiableMap(map);
    }

    static {
        Map<OperationType, Set<ServiceType>> map = new HashMap<>();
        // A set containing only the WMS ServiceType
        Set<ServiceType> wmsSet = Set.of(ServiceType.WMS);

        // A set containing only the WFS ServiceType
        Set<ServiceType> wfsSet = Set.of(ServiceType.WFS);

        // A set containing only the WCS ServiceType
        Set<ServiceType> wcsSet = Set.of(ServiceType.WCS);

        // A set containing only the WPS ServiceType
        Set<ServiceType> wpsSet = Set.of(ServiceType.WPS);

        // A set containing only the WMTS ServiceType
        Set<ServiceType> wmtsSet = Set.of(ServiceType.WMTS);

        // A set containing the WMS, WFS, WCS and WPS ServiceTypes
        Set<ServiceType> getCapSet = Set.of(ServiceType.WMS, ServiceType.WFS, ServiceType.WCS, ServiceType.WPS, ServiceType.WMTS);

        // look up all WMS operations from the previously created map
        Set<OperationType> wmsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WMS);
        // look up all WFS operations from the previously created map
        Set<OperationType> wfsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WFS);
        // look up all WCS operations from the previously created map
        Set<OperationType> wcsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WCS);
        // look up all WPS operations from the previously created map
        Set<OperationType> wpsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WPS);
        // look up all WMTS operations from the previously created map
        Set<OperationType> wmtsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WMTS);

        // put all ServiceTypes for the GetCapability operation
        map.put(OperationType.GET_CAPABILITIES, getCapSet);
        // for WMS operations, put the WMS set, unless it's the GetCapability op
        for (OperationType wmsOperation : wmsOperations) {
            if (!OperationType.GET_CAPABILITIES.equals(wmsOperation)) {
                map.put(wmsOperation, wmsSet);
            }
        }
        // for WFS operations, put the WFS set, unless it's the GetCapability op
        for (OperationType wfsOperation : wfsOperations) {
            if (!OperationType.GET_CAPABILITIES.equals(wfsOperation)) {
                map.put(wfsOperation, wfsSet);
            }
        }
        // for WCS operations, put the WCS set, unless it's the GetCapability op
        for (OperationType wcsOperation : wcsOperations) {
            if (!OperationType.GET_CAPABILITIES.equals(wcsOperation)) {
                map.put(wcsOperation, wcsSet);
            }
        }
        // for WPS operations, put the WPS set, unless it's the GetCapability op
        for (OperationType wpsOperation : wpsOperations) {
            if (!OperationType.GET_CAPABILITIES.equals(wpsOperation)) {
                map.put(wpsOperation, wpsSet);
            }
        }
        // for WMTS operations, put the WMTS set, unless it's the GetCapability op
        for (OperationType wmtsOperation : wmtsOperations) {
            if (!OperationType.GET_CAPABILITIES.equals(wmtsOperation)) {
                map.put(wmtsOperation, wmtsSet);
            }
        }
        // store it in the lookup
        SERVICETYPES_BY_OPERATION = Collections.unmodifiableMap(map);
    }

    /**
     * A enum type for the allowed service format.
     */
    public enum Service {
        SERVICE("SERVICE");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        Service(String value) {
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
        public static Service fromString(String inputValue) {
            if (inputValue != null) {
                for (Service type : Service.values()) {
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

    /**
     * A enum type for the allowed operation format.
     */
    public enum Operation {
        OPERATION("REQUEST");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        Operation(String value) {
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
        public static OperationType fromString(String inputValue) {
            if (inputValue != null) {
                for (OperationType type : OperationType.values()) {
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

    /**
     * A enum type for the allowed endPoint format.
     */
    public enum EndPoint {
        LAYERS("LAYERS"),
        LAYER("LAYER"),
        TYPENAME("TYPENAME"),
        TYPENAMES("TYPENAMES"),
        NAMESPACE("NAMESPACE");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        EndPoint(String value) {
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
        public static EndPoint fromString(String inputValue) {
            if (inputValue != null) {
                for (EndPoint type : EndPoint.values()) {
                    if (inputValue.equalsIgnoreCase(type.value)) {
                        return type;
                    }
                }
            }
            return null;
        }

        /**
         * Returns all enum values as string array.
         *
         * @return
         */
        public static String[] getAllValues() {
            EndPoint[] endPoints = values();
            String[] values = new String[endPoints.length];

            for (int i = 0; i < endPoints.length; i++) {
                values[i] = endPoints[i].value;
            }

            return values;
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

    /**
     * A enum type for the allowed service type format.
     */
    public enum ServiceType {
        WMS("WMS"),
        WMTS("WMTS"),
        WFS("WFS"),
        WCS("WCS"),
        WPS("WPS"),
        W3DS("W3DS");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        ServiceType(String value) {
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
        public static ServiceType fromString(String inputValue) {
            if (inputValue != null) {
                for (ServiceType type : ServiceType.values()) {
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

    /**
     * A enum type for the allowed operation type format.
     */
    public enum OperationType {
        GET_MAP("GetMap"),
        GET_CAPABILITIES("GetCapabilities"),
        GET_FEATURE_INFO("GetFeatureInfo"),
        DESCRIBE_LAYER("DescribeLayer"),
        GET_LEGEND_GRAPHIC("GetLegendGraphic"),
        GET_STYLES("GetStyles"),
        DESCRIBE_FEATURE_TYPE("DescribeFeatureType"),
        GET_FEATURE("GetFeature"),
        LOCK_FEATURE("LockFeature"),
        TRANSACTION("Transaction"),
        DESCRIBE_COVERAGE("DescribeCoverage"),
        GET_COVERAGE("GetCoverage"),
        EXECUTE("Execute"),
        DESCRIBE_PROCESS("DescribeProcess"),
        GET_SCENE("GetScene"),
        GET_LAYER_INFO("GetLayerInfo"),
        GET_TILE("GetTile");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        OperationType(String value) {
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
        public static OperationType fromString(String inputValue) {
            if (inputValue != null) {
                for (OperationType type : OperationType.values()) {
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
