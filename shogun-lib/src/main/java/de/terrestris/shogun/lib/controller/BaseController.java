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

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import de.terrestris.shogun.lib.controller.security.permission.BasePermissionController;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.ParameterizedType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

// TODO Specify and type extension of BaseService
@Log4j2
public abstract class BaseController<T extends BaseService<?, S>, S extends BaseEntity> extends BasePermissionController<T, S> {

    @Autowired
    protected T service;

    @Autowired
    protected MessageSource messageSource;

    @GetMapping(
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Returns all entities",
        security = { @SecurityRequirement(name = "bearer-key") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok: The entity was successfully created"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized: You need to provide a bearer token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found: The provided ID does not exist (or you don't have the permission to delete it)"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error: Something internal went wrong while deleting the entity"
        )
    })
    public Page<S> findAll(@PageableDefault(Integer.MAX_VALUE) Pageable pageable) {
        log.trace("Requested to return all entities of type {}", getGenericClassName());

        try {
            Page<S> persistedEntities = service.findAll(pageable);

            log.trace("Successfully got all entities of type {} (count: {})",
                getGenericClassName(), persistedEntities.getTotalElements());

            return persistedEntities;
        } catch (AccessDeniedException ade) {
            log.warn("Access to entity of type {} is denied", getGenericClassName());

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
            log.error("Error while requesting all entities of type {}: \n {}",
                    getGenericClassName(), e.getMessage());
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public S findOne(@PathVariable("id") Long entityId) {
        log.trace("Requested to return entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                S persistedEntity = entity.get();

                log.trace("Successfully got entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return persistedEntity;
            } else {
                log.error("Could not find entity of type {} with ID {}",
                        getGenericClassName(), entityId);

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
            log.warn("Access to entity of type {} with ID {} is denied",
                    getGenericClassName(), entityId);

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
            log.error("Error while requesting entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping("/{id}/rev")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public Revisions<Integer, S> findRevisions(@PathVariable("id") Long entityId) {
        log.trace("Requested to return all revisions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Revisions<Integer, S> revisions = service.findRevisions(entity.get());

                log.trace("Successfully got all revisions for entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return revisions;
            } else {
                log.error("Could not find entity of type {} with ID {}",
                    getGenericClassName(), entityId);

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
            log.warn("Access to entity of type {} with ID {} is denied",
                getGenericClassName(), entityId);

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
            log.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping("/{id}/rev/{rev}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public Revision<Integer, S> findRevision(@PathVariable("id") Long entityId, @PathVariable("rev") Integer rev) {
        log.trace("Requested to return revision {} for entity of type {} with ID {}",
            rev, getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Optional<Revision<Integer, S>> revision = service.findRevision(entity.get(), rev);

                if (revision.isPresent()) {
                    log.trace("Successfully got revision {} for entity of type {} with ID {}",
                        rev, getGenericClassName(), entityId);

                    return revision.get();
                } else {
                    log.error("Could not find revision {} for entity with ID {}",
                        rev, entityId);

                    throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage(
                            "BaseController.NOT_FOUND",
                            null,
                            LocaleContextHolder.getLocale()
                        )
                    );
                }
            } else {
                log.error("Could not find entity of type {} with ID {}",
                    getGenericClassName(), entityId);

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
            log.warn("Access to entity of type {} with ID {} is denied",
                getGenericClassName(), entityId);

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
            log.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping({"/{id}/forTime/{timeStamp}"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public S findOneByTime(
        @PathVariable("id") Long entityId, @PathVariable("timeStamp")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime timeStamp
    ) {
        log.trace("Requested to return entity of type {} with ID {} for date {}",
            getGenericClassName(), entityId, timeStamp);

        try {
            Optional<S> entity = service.findOneByTime(entityId, timeStamp);

            if (entity.isPresent()) {
                S persistedEntity = entity.get();

                log.trace("Successfully got entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return persistedEntity;
            } else {
                log.error("Could not find entity of type {} with ID {} for time {}",
                    getGenericClassName(), entityId, timeStamp);

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
            log.warn("Access to entity of type {} with ID {} is denied",
                getGenericClassName(), entityId);

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
            log.error("Error while requesting entity of type {} with ID {} for time {}: \n {}",
                getGenericClassName(), entityId, timeStamp, e.getMessage());
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

    @GetMapping("/{id}/lastrev")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public Revision<Integer, S> findLastChangeRevision(@PathVariable("id") Long entityId) {
        log.trace("Requested to return the latest revision for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Optional<Revision<Integer, S>> revision = service.findLastChangeRevision(entity.get());

                if (revision.isPresent()) {
                    log.trace("Successfully got the latest revision for entity of type {} with ID {}",
                        getGenericClassName(), entityId);

                    return revision.get();
                } else {
                    log.error("Could not find the latest revision for entity with ID {}", entityId);

                    throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage(
                            "BaseController.NOT_FOUND",
                            null,
                            LocaleContextHolder.getLocale()
                        )
                    );
                }
            } else {
                log.error("Could not find entity of type {} with ID {}",
                    getGenericClassName(), entityId);

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
            log.warn("Access to entity of type {} with ID {} is denied",
                getGenericClassName(), entityId);

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
            log.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public S add(@RequestBody S entity) {
        log.trace("Requested to create a new entity of type {} ({})",
            getGenericClassName(), entity);

        try {
            S persistedEntity = service.create(entity);

            log.trace("Successfully created the entity of type {} with ID {}",
                getGenericClassName(), persistedEntity.getId());

            return persistedEntity;
        } catch (AccessDeniedException ade) {
            log.warn("Creating entity of type {} is denied", getGenericClassName());

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
            log.error("Error while creating entity {}: \n {}", entity, e.getMessage());
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public S update(@RequestBody S entity, @PathVariable("id") Long entityId) {
        log.trace("Requested to update entity of type {} with ID {} ({})",
            getGenericClassName(), entityId, entity);

        try {
            if (!entityId.equals(entity.getId())) {
                log.error("IDs of update candidate (ID: {}) and update data ({}) don't match.",
                    entityId, entity);

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            Optional<S> persistedEntity = service.findOne(entityId);

            if (persistedEntity.isPresent()) {
                S updatedEntity = service.update(entityId, entity);

                log.trace("Successfully updated entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return updatedEntity;
            } else {
                log.error("Could not find entity of type {} with ID {}",
                        getGenericClassName(), entityId);

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
            log.warn("Updating entity of type {} with ID {} is denied",
                    getGenericClassName(), entityId);

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
            log.error("Error while updating entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public S updatePartial(@RequestBody JsonMergePatch patch, @PathVariable("id") Long entityId) {
        log.trace("Requested to partially update entity of type {} with ID {} ({})", getGenericClassName(), entityId, patch);

        try {
            S persistedEntity = service.findOne(entityId).orElseThrow();
            if (persistedEntity != null) {
                S updatedEntity = service.updatePartial(persistedEntity, patch);

                log.trace("Successfully updated values for entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return updatedEntity;
            } else {
                log.error("Could not find entity of type {} with ID {}",
                    getGenericClassName(), entityId);

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
            log.warn("Updating values of type {} with ID {} is denied",
                getGenericClassName(), entityId);

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    messageSource.getMessage(
                            "BaseController.NOT_FOUND",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    ade
            );
        } catch (NumberFormatException nfe) {
            log.error("Can't parse 'id' field ({}) from values ({}). It has to be an Integer.: {}",
                patch, entityId, nfe.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            log.error("Error while updating values for entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @DeleteMapping(
        value = "/{id}",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete entity by its ID",
        description = "TODO"
//        security = { @SecurityRequirement(name = "bearer-key") }
//        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Class<S>)) }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "No content: The entity was successfully deleted"
        ),
//        @ApiResponse(
//            responseCode = "400",
//            description = "Bad Request: "
//        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized: You need to provide a bearer token"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found: The provided ID does not exist (or you don't have the permission to delete it)"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error: Something internal went wrong while deleting the entity"
        )
    })
    public void delete(@Parameter(description = "id of the entity to delete") @PathVariable("id") Long entityId) {
        log.trace("Requested to delete entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                service.delete(entity.get());

                log.trace("Successfully deleted entity of type {} with ID {}",
                    getGenericClassName(), entityId);
            } else {
                log.error("Could not find entity of type {} with ID {}",
                    getGenericClassName(), entityId);

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
            log.warn("Deleting entity of type {} with ID {} is denied",
                getGenericClassName(), entityId);

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
            log.error("Error while deleting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    protected String getGenericClassName() {
        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
            BaseController.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1].getSimpleName();
        } else {
            return null;
        }
    }
}
