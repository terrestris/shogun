package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.TextualContentDto;
import de.terrestris.shogun.lib.model.TextualContent;
import org.springframework.stereotype.Component;

@Component
public class TextualContentMapper extends BaseEntityMapper<
    TextualContent,
    TextualContentDto.Read,
    TextualContentDto.Create,
    TextualContentDto.Update
> {

    @Override
    public TextualContentDto.Read toReadDto(TextualContent persistedEntity) {
        TextualContentDto.Read readDto = new TextualContentDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        readDto.setTitle(persistedEntity.getTitle());
        readDto.setCategory(persistedEntity.getCategory());
        readDto.setMarkdown(persistedEntity.getMarkdown());

        return readDto;
    }

    @Override
    public TextualContent fromCreateDto(TextualContentDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        TextualContent entity = new TextualContent();
        entity.setTitle(createDto.getTitle());
        entity.setCategory(createDto.getCategory());
        entity.setMarkdown(createDto.getMarkdown());

        return entity;
    }

    @Override
    public TextualContent fromUpdateDto(TextualContent persistedEntity, TextualContentDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setTitle(updateDto.getTitle());
        persistedEntity.setCategory(updateDto.getCategory());
        persistedEntity.setMarkdown(updateDto.getMarkdown());

        return persistedEntity;
    }
}
