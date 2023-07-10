/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class DefaultMapView implements Serializable {

    @Schema(
        description = "The initial zoom level of the map.",
        example = "1"
    )
    private Integer zoom;

    @Schema(
        description = "The initial center of the map (in WGS84).",
        example = "[10.3, 51.08]"
    )
    private ArrayList<Double> center;

    @Schema(
        description = "The maximum extent of the map (in WGS84).",
        example = "[2.5683045738288137, 45.429089001638076, 19.382621082401887, 57.283993958205926]"
    )
    private ArrayList<Double> extent;

    @Schema(
        description = "The projection of the map.",
        example = "EPSG:25832"
    )
    private String projection;

    @Schema(
        description = "The list of CRS definitions in proj4 format that should be registered in the application additionally.",
        example = "{\"crsCode\": \"EPSG:25832\", \"definition\": \"+proj=utm +zone=32 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs\"}"
    )
    private ArrayList<DefaultCrsDefinition> crsDefinitions;

    @Schema(
        description = "The list of resolutions/zoom levels of the map.",
        example = "[2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317017, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135, 0.298582142, 0.149291071, 0.074645535]"
    )
    private ArrayList<Double> resolutions;

}
