package de.terrestris.shogun.lib.specification.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import org.springframework.data.jpa.domain.Specification;

public class UserInstancePermissionSpecifications {

    public static Specification<UserInstancePermission> hasEntity(BaseEntity entity) {
        return (root, query, cb) -> cb.equal(root.get("entity"), entity);
    }

    public static Specification<UserInstancePermission> hasUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}
