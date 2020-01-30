package de.terrestris.shoguncore.service.security.permission;

import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class PermissionCollectionService extends BaseService<PermissionCollectionRepository, PermissionCollection> {
}
