package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.LayerDto;
import de.terrestris.shogun.lib.model.Layer;
import org.springframework.stereotype.Component;

@Component
public class LayerMapper extends BaseEntityMapper<
    Layer,
    LayerDto.Read,
    LayerDto.Create,
    LayerDto.Update
> {

    @Override
    public LayerDto.Read toReadDto (Layer persistedEntity) {
        LayerDto.Read readDto = new LayerDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        // Set entity-specific fields
        readDto.setName(persistedEntity.getName());
        readDto.setType(persistedEntity.getType());
        readDto.setClientConfig(persistedEntity.getClientConfig());
        readDto.setFeatures(persistedEntity.getFeatures());
        readDto.setSourceConfig(persistedEntity.getSourceConfig());

        return readDto;
    }

    @Override
    public Layer fromCreateDto(LayerDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        Layer entity = new Layer();
        entity.setName(createDto.getName());
        entity.setType(createDto.getType());
        entity.setClientConfig(createDto.getClientConfig());
        entity.setFeatures(createDto.getFeatures());
        entity.setSourceConfig(createDto.getSourceConfig());

        return entity;
    }

    @Override
    public Layer fromUpdateDto(Layer persistedEntity, LayerDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setName(updateDto.getName());
        persistedEntity.setType(updateDto.getType());
        persistedEntity.setClientConfig(updateDto.getClientConfig());
        persistedEntity.setFeatures(updateDto.getFeatures());
        persistedEntity.setSourceConfig(updateDto.getSourceConfig());

        return persistedEntity;
    }
}
