package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.RoleDto;
import de.terrestris.shogun.lib.dto.model.UserDto;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper extends BaseEntityMapper<
    Role,
    RoleDto.Read,
    RoleDto.Create,
    RoleDto.Update
> {

    @Override
    public RoleDto.Read toReadDto(Role persistedEntity) {
        RoleDto.Read readDto = new RoleDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        readDto.setAuthProviderId(persistedEntity.getAuthProviderId());
        readDto.setProviderDetails(persistedEntity.getProviderDetails());

        return readDto;
    }

    @Override
    public Role fromCreateDto(RoleDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        Role entity = new Role();
        entity.setAuthProviderId(createDto.getAuthProviderId());

        return entity;
    }

    @Override
    public Role fromUpdateDto(Role persistedEntity, RoleDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setAuthProviderId(updateDto.getAuthProviderId());

        return persistedEntity;
    }
}
