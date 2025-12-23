package de.terrestris.shogun.lib.mapper;

import de.terrestris.shogun.lib.dto.model.UserDto;
import de.terrestris.shogun.lib.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends BaseEntityMapper<
    User,
    UserDto.Read,
    UserDto.Create,
    UserDto.Update
> {

    @Override
    public UserDto.Read toReadDto (User persistedEntity) {
        UserDto.Read readDto = new UserDto.Read();

        // Copy common read-only fields (id, created, modified) - inherited from BaseEntityMapper
        copyReadOnlyFields(persistedEntity, readDto);

        readDto.setAuthProviderId(persistedEntity.getAuthProviderId());
        readDto.setProviderDetails(persistedEntity.getProviderDetails());
        readDto.setClientConfig(persistedEntity.getClientConfig());
        readDto.setAuthProviderId(persistedEntity.getAuthProviderId());

        return readDto;
    }

    @Override
    public User fromCreateDto(UserDto.Create createDto) {
        if (createDto == null) {
            return null;
        }

        User entity = new User();
        entity.setAuthProviderId(createDto.getAuthProviderId());
        entity.setClientConfig(createDto.getClientConfig());
        entity.setDetails(createDto.getDetails());

        return entity;
    }

    @Override
    public User fromUpdateDto(User persistedEntity, UserDto.Update updateDto) {
        if (updateDto == null || persistedEntity == null) {
            return null;
        }

        // Update the persisted entity with values from the DTO
        // The ID cannot be set directly as BaseEntity doesn't have a setter for it
        persistedEntity.setAuthProviderId(updateDto.getAuthProviderId());
        persistedEntity.setClientConfig(updateDto.getClientConfig());
        persistedEntity.setDetails(updateDto.getDetails());

        return persistedEntity;
    }
}
