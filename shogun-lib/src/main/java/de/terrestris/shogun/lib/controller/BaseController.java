package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.service.BaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO Specify and type extension of BaseService
public abstract class BaseController<T extends BaseService<?, S>, S extends BaseEntity> {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    protected T service;

    @Autowired
    protected MessageSource messageSource;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<S> findAll() {
        LOG.trace("Requested to return all entities of type {}", getGenericClassName());

        try {
            List<S> persistedEntities = service.findAll();

            LOG.trace("Successfully got all entities of type {} (count: {})",
                getGenericClassName(), persistedEntities.size());

            return persistedEntities;
        } catch (AccessDeniedException ade) {
            LOG.warn("Access to entity of type {} is denied", getGenericClassName());

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public S findOne(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to return entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                S persistedEntity = entity.get();

                LOG.trace("Successfully got entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return persistedEntity;
            } else {
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Access to entity of type {} with ID {} is denied",
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
            LOG.error("Error while requesting entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping("/{id}/rev")
    @ResponseStatus(HttpStatus.OK)
    public Revisions<Integer, S> findRevisions(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to return all revisions for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Revisions<Integer, S> revisions = service.findRevisions(entity.get());

                LOG.trace("Successfully got all revisions for entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return revisions;
            } else {
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Access to entity of type {} with ID {} is denied",
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
            LOG.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping("/{id}/rev/{rev}")
    @ResponseStatus(HttpStatus.OK)
    public Revision<Integer, S> findRevision(@PathVariable("id") Long entityId, @PathVariable("rev") Integer rev) {
        LOG.trace("Requested to return revision {} for entity of type {} with ID {}",
            rev, getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Optional<Revision<Integer, S>> revision = service.findRevision(entity.get(), rev);

                if (revision.isPresent()) {
                    LOG.trace("Successfully got revision {} for entity of type {} with ID {}",
                        rev, getGenericClassName(), entityId);

                    return revision.get();
                } else {
                    LOG.error("Could not find revision {} for entity with ID {}",
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
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Access to entity of type {} with ID {} is denied",
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
            LOG.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @GetMapping("/{id}/lastrev")
    @ResponseStatus(HttpStatus.OK)
    public Revision<Integer, S> findLastChangeRevision(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to return the latest revision for entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                Optional<Revision<Integer, S>> revision = service.findLastChangeRevision(entity.get());

                if (revision.isPresent()) {
                    LOG.trace("Successfully got the latest revision for entity of type {} with ID {}",
                        getGenericClassName(), entityId);

                    return revision.get();
                } else {
                    LOG.error("Could not find the latest revision for entity with ID {}", entityId);

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
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Access to entity of type {} with ID {} is denied",
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
            LOG.error("Error while requesting entity of type {} with ID {}: \n {}",
                getGenericClassName(), entityId, e.getMessage());
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public S add(@RequestBody S entity) {
        LOG.trace("Requested to create a new entity of type {} ({})",
            getGenericClassName(), entity);

        try {
            S persistedEntity = service.create(entity);

            LOG.trace("Successfully created the entity of type {} with ID {}",
                getGenericClassName(), persistedEntity.getId());

            return persistedEntity;
        } catch (AccessDeniedException ade) {
            LOG.warn("Creating entity of type {} is denied", getGenericClassName());

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
            LOG.error("Error while creating entity {}: \n {}", entity, e.getMessage());
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public S update(@RequestBody S entity, @PathVariable("id") Long entityId) {
        LOG.trace("Requested to update entity of type {} with ID {} ({})",
            getGenericClassName(), entityId, entity);

        try {
            if (!entityId.equals(entity.getId())) {
                LOG.error("IDs of update candidate (ID: {}) and update data ({}) don't match.",
                    entityId, entity);

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            Optional<S> persistedEntity = service.findOne(entityId);

            if (persistedEntity.isPresent()) {
                S updatedEntity = service.update(entityId, entity);

                LOG.trace("Successfully updated entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return updatedEntity;
            } else {
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Updating entity of type {} with ID {} is denied",
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
            LOG.error("Error while updating entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public S updatePartial(@RequestBody Map<String, Object> values, @PathVariable("id") Long entityId) {
        LOG.trace("Requested to partially update entity of type {} with ID {} ({})", getGenericClassName(), entityId, values);

        try {
            Object idFromValues = values.get("id");
            if (idFromValues == null) {
                LOG.error("Field 'id' is missing in the passed values.", entityId, values);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            Long id = Long.valueOf((Integer) idFromValues);
            if (!entityId.equals(id)) {
                LOG.error("IDs of update candidate (ID: {}) and update data ({}) don't match.", entityId, values);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            Optional<S> persistedEntity = service.findOne(entityId);

            if (persistedEntity.isPresent()) {
                S updatedEntity = service.updatePartial(entityId, values);

                LOG.trace("Successfully updated values for entity of type {} with ID {}",
                    getGenericClassName(), entityId);

                return updatedEntity;
            } else {
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Updating values of type {} with ID {} is denied",
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
            LOG.error("Error while updating values for entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long entityId) {
        LOG.trace("Requested to delete entity of type {} with ID {}",
            getGenericClassName(), entityId);

        try {
            Optional<S> entity = service.findOne(entityId);

            if (entity.isPresent()) {
                service.delete(entity.get());

                LOG.trace("Successfully deleted entity of type {} with ID {}",
                    getGenericClassName(), entityId);
            } else {
                LOG.error("Could not find entity of type {} with ID {}",
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
            LOG.warn("Deleting entity of type {} with ID {} is denied",
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
            LOG.error("Error while deleting entity of type {} with ID {}: \n {}",
                    getGenericClassName(), entityId, e.getMessage());
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
                BaseController.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[1].getSimpleName();
        } else {
            return null;
        }
    }
}
