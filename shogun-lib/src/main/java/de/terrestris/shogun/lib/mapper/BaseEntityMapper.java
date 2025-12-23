package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.BaseEntityDto;
import de.terrestris.shogun.lib.model.BaseEntity;

/**
 * Base mapper class for converting between entities and DTOs.
 *
 * Allows DTO inheritance for code reuse (e.g., Update extends Create).
 */
public abstract class BaseEntityMapper<
    T extends BaseEntity,
    R,  // Read DTO type
    C,  // Create DTO type
    U   // Update DTO type
> implements IBaseEntityMapper<T, R, C, U> {

    /**
     * Copies common read-only fields (id, created, modified) from an entity to a DTO.
     * This method is type-safe and does not use reflection.
     *
     * @param source The source entity
     * @param target The target DTO (must implement HasReadOnlyFields)
     * @param <D> The DTO type that implements HasReadOnlyFields
     */
    protected <D extends BaseEntityDto.ReadOnlyFields> void copyReadOnlyFields(T source, D target) {
        if (source == null || target == null) {
            return;
        }

        target.setId(source.getId());
        target.setCreated(source.getCreated());
        target.setModified(source.getModified());
    }
}
