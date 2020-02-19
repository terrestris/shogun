package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.User;

// https://insource.io/blog/articles/custom-authorization-with-spring-boot.html
public interface EntityPermissionEvaluator<E> {
    Class<E> getEntityClassName();

    boolean hasPermission(User user, E entity, PermissionType permission);
}
