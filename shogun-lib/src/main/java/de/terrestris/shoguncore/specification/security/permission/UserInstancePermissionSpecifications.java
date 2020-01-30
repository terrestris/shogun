package de.terrestris.shoguncore.specification.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.UserClassPermission;
import de.terrestris.shoguncore.model.security.permission.UserInstancePermission;
import org.springframework.data.jpa.domain.Specification;

public class UserInstancePermissionSpecifications {

    public static Specification<UserInstancePermission> hasEntity(BaseEntity entity) {
        return (root, query, cb) -> cb.equal(root.get("entity"), entity);
    }

    public static Specification<UserInstancePermission> hasUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}
