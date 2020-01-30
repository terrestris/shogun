package de.terrestris.shoguncore.specification.security.permission;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.security.permission.GroupClassPermission;
import de.terrestris.shoguncore.model.security.permission.UserClassPermission;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GroupClassPermissionSpecifications {

    public static Specification<GroupClassPermission> hasEntity(BaseEntity entity) {
        return (root, query, cb) -> cb.equal(root.get("className"), entity.getClass().getCanonicalName());
    }

    public static Specification<GroupClassPermission> hasEntity(Class<? extends BaseEntity> clazz) {
        return (root, query, cb) -> cb.equal(root.get("className"), clazz.getCanonicalName());
    }

    public static Specification<GroupClassPermission> hasGroup(Group group) {
        return (root, query, cb) -> cb.equal(root.get("group"), group);
    }

    public static Specification<GroupClassPermission> hasGroups(List<Group> groups) {
        return (root, query, cb) -> root.get("group").in(groups);
    }
}
