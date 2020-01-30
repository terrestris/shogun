package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.User;

// https://insource.io/blog/articles/custom-authorization-with-spring-boot.html
public interface EntityPermissionEvaluator<E> {
    Class<E> getEntityClassName();

    boolean hasPermission(User user, E entity, PermissionType permission);
}
