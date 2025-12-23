package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.model.BaseEntity;

/**
 * Base mapper interface for converting between entities and DTOs.
 *
 * The type parameters are deliberately unbounded to allow maximum flexibility,
 * including DTO inheritance (e.g., Update extends Create, Read extends Create).
 *
 * @param <T> The entity type
 * @param <R> The Read DTO type
 * @param <C> The Create DTO type
 * @param <U> The Update DTO type
 */
public interface IBaseEntityMapper<
    T extends BaseEntity,
    R,  // Read DTO type (no bounds - allows inheritance)
    C,  // Create DTO type (no bounds - allows inheritance)
    U   // Update DTO type (no bounds - allows inheritance)
> {
    R toReadDto(T persistedEntity);

    T fromCreateDto(C createDto);

    T fromUpdateDto(T persistedEntity, U updateDto);
}
