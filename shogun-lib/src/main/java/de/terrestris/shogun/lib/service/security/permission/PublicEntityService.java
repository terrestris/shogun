package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.security.permission.PublicEntity;
import de.terrestris.shogun.lib.repository.security.permission.PublicEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicEntityService {

    @Autowired
    private PublicEntityRepository publicEntityRepository;

    public void setPublic(BaseEntity baseEntity, boolean isPublic) {
        if (isPublic) {
            PublicEntity publicEntity = new PublicEntity();
            publicEntity.setId(baseEntity.getId());
            publicEntityRepository.save(publicEntity);
        } else {
            publicEntityRepository.deleteById(baseEntity.getId());
        }
    }

    public boolean getPublic(BaseEntity entityId) {
        return publicEntityRepository.findById(entityId.getId()).isPresent();
    }
}
