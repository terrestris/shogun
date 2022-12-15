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
package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.service.ImageFileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/imagefiles")
@ConditionalOnExpression("${controller.imagefiles.enabled:true}")
@Log4j2
@Tag(
    name = "ImageFiles",
    description = "The endpoints to manage image files"
)
@SecurityRequirement(name = "bearer-key")
public class ImageFileController extends BaseFileController<ImageFileService, ImageFile> {

    @GetMapping("/{fileUuid}/thumbnail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findOneThumbnail(@PathVariable("fileUuid") UUID fileUuid) {

        log.debug("Requested to return thumbnail for image file with UUID {}", fileUuid);

        try {
            Optional<ImageFile> entity = service.findOne(fileUuid);

            if (entity.isPresent()) {
                ImageFile file = entity.get();

                log.info("Successfully got thumbnail for image file with UUID {}", fileUuid);

                final HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.IMAGE_PNG);
                String thumbnailFileName = file.getFileName().split("\\.")[0] + ".png";
                responseHeaders.setContentDisposition(ContentDisposition.parse(
                    String.format("inline; filename=\"%s\"", thumbnailFileName)));

                return new ResponseEntity<>(file.getThumbnail(), responseHeaders, HttpStatus.OK);
            } else {
                log.error("Could not find entity of type {} with UUID {}",
                    getGenericClassName(), fileUuid);

                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    messageSource.getMessage(
                        "BaseController.NOT_FOUND",
                        null,
                        LocaleContextHolder.getLocale()
                    )
                );
            }
        } catch (AccessDeniedException ade) {
            log.info("Access to entity of type {} with UUID {} is denied",
                getGenericClassName(), fileUuid);

            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                messageSource.getMessage(
                    "BaseController.NOT_FOUND",
                    null,
                    LocaleContextHolder.getLocale()
                ),
                ade
            );
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            log.error("Error while requesting entity of type {} with UUID {}: \n {}",
                getGenericClassName(), fileUuid, e.getMessage());
            log.trace("Full stack trace: ", e);

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage(
                    "BaseController.INTERNAL_SERVER_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                ),
                e
            );
        }
    }

}
