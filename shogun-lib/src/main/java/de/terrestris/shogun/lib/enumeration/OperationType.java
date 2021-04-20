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

    private final String type;

    private OperationType(String type) {
        this.type = type;
    }
}
