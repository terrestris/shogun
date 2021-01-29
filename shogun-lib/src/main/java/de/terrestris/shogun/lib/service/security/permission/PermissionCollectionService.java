package de.terrestris.shogun.lib.service.security.permission;

import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shogun.lib.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class PermissionCollectionService extends BaseService<PermissionCollectionRepository, PermissionCollection> { }
