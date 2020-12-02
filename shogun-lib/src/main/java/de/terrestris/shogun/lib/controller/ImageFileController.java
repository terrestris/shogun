package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.ImageFile;
import de.terrestris.shogun.lib.service.ImageFileService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/imagefiles")
@ConditionalOnExpression("${controller.imagefiles.enabled:true}")
public class ImageFileController extends BaseFileController<ImageFileService, ImageFile> {

    @GetMapping("/{fileUuid}/thumbnail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findOneThumbnail(@PathVariable("fileUuid") UUID fileUuid) {

        LOG.debug("Requested to return thumbnail for image file with UUID {}", fileUuid);

        try {
            Optional<ImageFile> entity = service.findOne(fileUuid);

            if (entity.isPresent()) {
                ImageFile file = entity.get();

                LOG.info("Successfully got thumbnail for image file with UUID {}", fileUuid);

                final HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.parseMediaType(file.getFileType()));
                responseHeaders.setContentDisposition(ContentDisposition.parse(
                    String.format("inline; filename=\"%s\"", file.getFileName())));

                return new ResponseEntity<>(file.getThumbnail(), responseHeaders, HttpStatus.OK);
            } else {
                LOG.error("Could not find entity of type {} with UUID {}",
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
            LOG.info("Access to entity of type {} with UUID {} is denied",
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
            LOG.error("Error while requesting entity of type {} with UUID {}: \n {}",
                getGenericClassName(), fileUuid, e.getMessage());
            LOG.trace("Full stack trace: ", e);

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
