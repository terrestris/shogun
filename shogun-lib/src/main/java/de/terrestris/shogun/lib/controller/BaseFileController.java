package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.BaseFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseFileController<T extends BaseFileService<?, S>, S extends File> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected T service;

    @Autowired
    protected MessageSource messageSource;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<S> findAll(@PageableDefault(Integer.MAX_VALUE) Pageable pageable) {
        LOG.trace("Requested to return all entities of type {}", getGenericClassName());

        try {
            Page<S> persistedEntities = service.findAll(pageable);

            LOG.trace("Successfully got all entities of type {} (count: {})",
                getGenericClassName(), persistedEntities.getTotalElements());

            return persistedEntities;
        } catch (AccessDeniedException ade) {
            LOG.info("Access to entity of type {} is denied", getGenericClassName());

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
            LOG.error("Error while requesting all entities of type {}: \n {}",
                getGenericClassName(), e.getMessage());
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

    @GetMapping("/{fileUuid}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findOne(@PathVariable("fileUuid") UUID fileUuid) {
        LOG.debug("Requested to return file with UUID {}", fileUuid);

        try {
            Optional<S> entity = service.findOne(fileUuid);

            if (entity.isPresent()) {
                S file = entity.get();

                LOG.info("Successfully got file with UUID {}", fileUuid);

                final HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.parseMediaType(file.getFileType()));
                responseHeaders.setContentDisposition(ContentDisposition.parse(
                    String.format("inline; filename=\"%s\"", file.getFileName())));

                return new ResponseEntity<>(file.getFile(), responseHeaders, HttpStatus.OK);
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

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public S add(MultipartFile uploadedFile) {
        LOG.debug("Requested to upload a multipart-file");

        try {

            service.isValidType(uploadedFile.getContentType());

            S persistedFile = service.create(uploadedFile);

            LOG.info("Successfully uploaded file " + persistedFile.getFileName());

            return persistedFile;
        } catch (AccessDeniedException ade) {
            LOG.info("Uploading entity of type {} is denied", getGenericClassName());

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
            LOG.error("Could not upload the file: " + e.getMessage());
            LOG.trace("Full stack trace: ", e);

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage(
                    "BaseController.INTERNAL_SERVER_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                )
            );
        }
    }

    @DeleteMapping("/{fileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("fileUuid") UUID fileUuid) {
        LOG.trace("Requested to delete entity of type {} with UUID {}",
            getGenericClassName(), fileUuid);

        try {
            Optional<S> entity = service.findOne(fileUuid);

            if (entity.isPresent()) {
                service.delete(entity.get());

                LOG.trace("Successfully deleted entity of type {} with UUID {}",
                    getGenericClassName(), fileUuid);
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
            LOG.info("Deleting entity of type {} with UUID {} is denied",
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
            LOG.error("Error while deleting entity of type {} with UUID {}: \n {}",
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

    protected String getGenericClassName() {
        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
            BaseFileController.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1].getSimpleName();
        } else {
            return null;
        }
    }
}
