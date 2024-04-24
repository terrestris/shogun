package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.security.permission.PublicEntity;
import de.terrestris.shogun.lib.repository.security.permission.PublicEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PublicEntityService {

    @Autowired
    private PublicEntityRepository publicEntityRepository;

    @PreAuthorize("hasRole('ADMIN') or hasPermission(#entity, 'UPDATE')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setPublic(BaseEntity entity, boolean isPublic) {
        if (isPublic) {
            Optional<PublicEntity> publicOpt = publicEntityRepository.findByEntityId(entity.getId());
            if (publicOpt.isPresent()) {
                return;
            }
            PublicEntity publicEntity = new PublicEntity();
            publicEntity.setEntityId(entity.getId());
            publicEntityRepository.save(publicEntity);
        } else {
            publicEntityRepository.deleteByEntityId(entity.getId());
        }
    }

    public boolean getPublic(BaseEntity entityId) {
        return publicEntityRepository.findByEntityId(entityId.getId()).isPresent();
    }
}
