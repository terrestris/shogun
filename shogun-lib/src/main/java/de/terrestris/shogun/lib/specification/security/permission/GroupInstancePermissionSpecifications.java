package de.terrestris.shogun.lib.specification.security.permission;

import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.security.permission.GroupInstancePermission;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GroupInstancePermissionSpecifications {

    public static Specification<GroupInstancePermission> hasEntity(BaseEntity entity) {
        return (root, query, cb) -> cb.equal(root.get("entity"), entity);
    }

    public static Specification<GroupInstancePermission> hasGroup(Group group) {
        return (root, query, cb) -> cb.equal(root.get("group"), group);
    }

    public static Specification<GroupInstancePermission> hasGroups(List<Group> groups) {
        return (root, query, cb) -> root.get("group").in(groups);
    }
}
