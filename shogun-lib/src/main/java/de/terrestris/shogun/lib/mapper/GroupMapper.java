package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.GroupDto;
import de.terrestris.shogun.lib.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper extends BaseEntityMapper<
    Group,
    GroupDto.Read,
    GroupDto.Create,
    GroupDto.Update
> {

    @Override
    public GroupDto.Read toReadDto(Group persistedEntity) {
        GroupDto.Read readDto = new GroupDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        readDto.setAuthProviderId(persistedEntity.getAuthProviderId());
        readDto.setProviderDetails(persistedEntity.getProviderDetails());

        return readDto;
    }

    @Override
    public Group fromCreateDto(GroupDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        Group entity = new Group();
        entity.setAuthProviderId(createDto.getAuthProviderId());

        return entity;
    }

    @Override
    public Group fromUpdateDto(Group persistedEntity, GroupDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setAuthProviderId(updateDto.getAuthProviderId());

        return persistedEntity;
    }
}
