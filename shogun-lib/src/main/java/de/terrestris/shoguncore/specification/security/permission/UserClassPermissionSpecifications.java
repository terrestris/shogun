package de.terrestris.shoguncore.specification.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.UserClassPermission;
import de.terrestris.shoguncore.model.security.permission.UserInstancePermission;
import org.springframework.data.jpa.domain.Specification;

public class UserClassPermissionSpecifications {

    public static Specification<UserClassPermission> hasEntity(BaseEntity entity) {
        return (root, query, cb) -> cb.equal(root.get("className"), entity.getClass().getCanonicalName());
    }

    public static Specification<UserClassPermission> hasEntity(Class<? extends BaseEntity> clazz) {
        return (root, query, cb) -> cb.equal(root.get("className"), clazz.getCanonicalName());
    }

    public static Specification<UserClassPermission> hasUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}
