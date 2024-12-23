/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.boot.dto.ProjectionInfo;
import de.terrestris.shogun.boot.dto.TransformResult;
import de.terrestris.shogun.boot.service.ProjectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Controller
@Log4j2
@Tag(
    name = "Projections",
    description = "Endpoints to work with projections"
)
public class ProjectionController {

    @Autowired
    private ProjectionService projectionService;

    @GetMapping(path = "/epsg")
    @Operation(
        summary = "Return details for a projection. Follows the format of the https://epsg.io API"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok: the details were found and returned"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error: Something went wrong trying to access the CRS definition"
        )
    })
    public ResponseEntity<ProjectionInfo> getProjectionDetails(@RequestParam(name = "q") String query) {
        try {
            return new ResponseEntity<>(projectionService.getProjectionDetails(query), OK);
        } catch (Exception e) {
            log.warn("Unable to find projection: {}", e.getMessage());
            log.trace("Stack trace:", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/epsg/transform")
    @Operation(
        summary = "Transform a geometry from one projection to another"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok: the geometry was successfully transformed"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error: Something went wrong trying transform the geometry"
        )
    })
    public ResponseEntity<TransformResult> transform(
        @RequestParam(name = "source") String source,
        @RequestParam(name = "target") String target,
        @RequestParam(name = "wkt") String wkt
    ) {
        try {
            return new ResponseEntity<>(projectionService.transform(source, target, wkt), OK);
        } catch (Exception e) {
            log.warn("Unable to transform: {}", e.getMessage());
            log.trace("Stack trace:", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

}
