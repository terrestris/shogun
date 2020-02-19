package de.terrestris.shogun.lib.specification.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import org.springframework.data.jpa.domain.Specification;

public class PermissionCollectionSpecification {

    public static Specification<PermissionCollection> findByName(PermissionCollectionType name) {
        return (root, query, cb) -> {
            return cb.equal(root.get("name"), name);
        };
    }
}
