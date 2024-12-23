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
package de.terrestris.shogun.boot.service;

import de.terrestris.shogun.boot.dto.ProjectionDetails;
import de.terrestris.shogun.boot.dto.ProjectionInfo;
import de.terrestris.shogun.boot.dto.TransformResult;
import lombok.extern.log4j.Log4j2;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;
import org.geotools.referencing.CRS;
import org.geotools.referencing.proj.PROJFormattable;
import org.geotools.referencing.proj.PROJFormatter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ProjectionService {

    public ProjectionInfo getProjectionDetails(String query) throws FactoryException {
        var projectionInfo = new ProjectionInfo();
        var projectionDetails = new ProjectionDetails();
        projectionInfo.getResults().add(projectionDetails);
        var system = CRS.decode(query);
        var identifier = system.getIdentifiers().iterator().next();
        projectionDetails.setAuthority(identifier.getAuthority().getTitle().toString());
        projectionDetails.setCode(identifier.getCode());
        projectionDetails.setName(system.getName().toString());
        var fmt = new PROJFormatter();
        projectionDetails.setProj4(fmt.toPROJ((PROJFormattable) system));
        var bbox = system.getDomainOfValidity();
        var bboxList = projectionDetails.getBbox();
        for (var item : bbox.getGeographicElements()) {
            var box = ((GeographicBoundingBoxImpl) item);
            bboxList.add(box.getNorthBoundLatitude());
            bboxList.add(box.getWestBoundLongitude());
            bboxList.add(box.getSouthBoundLatitude());
            bboxList.add(box.getEastBoundLongitude());
        }
        projectionDetails.setUnit(system.getCoordinateSystem().getAxis(0).getUnit().getName().toLowerCase());
        projectionDetails.setArea(bbox.getDescription().toString());
        projectionDetails.setWkt(system.toWKT());
        return projectionInfo;
    }

    public TransformResult transform(String sourceCrs, String targetCrs, String wkt) throws FactoryException, ParseException, TransformException {
        var source = CRS.decode(sourceCrs);
        var target = CRS.decode(targetCrs);
        var geometry = new WKTReader().read(wkt);
        var transform = CRS.findMathTransform(source, target);
        Geometry transformed = JTS.transform(geometry, transform);
        var result = new WKTWriter().write(transformed);
        return new TransformResult(result);
    }

}
