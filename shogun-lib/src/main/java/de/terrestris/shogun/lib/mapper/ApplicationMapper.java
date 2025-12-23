package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.ApplicationDto;
import de.terrestris.shogun.lib.model.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper extends BaseEntityMapper<
    Application,
    ApplicationDto.Read,
    ApplicationDto.Create,
    ApplicationDto.Update
> {

    @Override
    public ApplicationDto.Read toReadDto(Application persistedEntity) {
        ApplicationDto.Read readDto = new ApplicationDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        // Set entity-specific fields
        readDto.setName(persistedEntity.getName());
        readDto.setStateOnly(persistedEntity.getStateOnly());
        readDto.setClientConfig(persistedEntity.getClientConfig());
        readDto.setLayerTree(persistedEntity.getLayerTree());
        readDto.setLayerConfig(persistedEntity.getLayerConfig());
        readDto.setToolConfig(persistedEntity.getToolConfig());

        return readDto;
    }

    @Override
    public Application fromCreateDto(ApplicationDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        Application entity = new Application();
        entity.setName(createDto.getName());
        entity.setStateOnly(createDto.getStateOnly());
        entity.setClientConfig(createDto.getClientConfig());
        entity.setLayerTree(createDto.getLayerTree());
        entity.setLayerConfig(createDto.getLayerConfig());
        entity.setToolConfig(createDto.getToolConfig());

        return entity;
    }

    @Override
    public Application fromUpdateDto(Application persistedEntity, ApplicationDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setName(updateDto.getName());
        persistedEntity.setStateOnly(updateDto.getStateOnly());
        persistedEntity.setClientConfig(updateDto.getClientConfig());
        persistedEntity.setLayerTree(updateDto.getLayerTree());
        persistedEntity.setLayerConfig(updateDto.getLayerConfig());
        persistedEntity.setToolConfig(updateDto.getToolConfig());

        return persistedEntity;
    }
}
