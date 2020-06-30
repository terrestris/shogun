package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.util.IdHelper;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class BaseEntityPermissionEvaluatorTest<E extends BaseEntity> {

    private Class<E> entityClass;

//    @InjectMocks
    private BaseEntityPermissionEvaluator<E> baseEntityPermissionEvaluator;

    private E entityToCheck;

    protected BaseEntityPermissionEvaluatorTest(Class<E> entityClass, BaseEntityPermissionEvaluator<E> baseEntityPermissionEvaluator, E entityToCheck) throws NoSuchFieldException {
        this.entityClass = entityClass;
        this.baseEntityPermissionEvaluator = baseEntityPermissionEvaluator;
        this.entityToCheck = entityToCheck;

        IdHelper.setIdForEntity(this.entityToCheck, 123L);
    }

    @Test
    public void hasPermission_shouldNeverGrantAnythingWithoutPermissions() throws NoSuchFieldException {
        final User user = new User();
        user.setKeycloakId("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Set<PermissionType> allPermissions = new HashSet<>(Arrays.asList(PermissionType.values()));

        for (PermissionType permission : allPermissions) {
            boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertFalse(permissionResult);
        }
    }

    @Test
    public void hasPermission_shouldGrantPermissionOnSecuredObjectWithCorrectUserPermission() throws NoSuchFieldException, IllegalAccessException {
        final User user = new User();
        user.setKeycloakId("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Map<User, PermissionCollection> userPermissionsMap = new HashMap<>();

        PermissionType updatePermission = PermissionType.UPDATE;
        PermissionCollection permissionCollection = buildPermissionCollection(PermissionCollectionType.UPDATE, updatePermission);
        userPermissionsMap.put(user, permissionCollection);
        // TODO Use service!
//        entityToCheck.setUserPermissions(userPermissionsMap);

        boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, updatePermission);

        assertTrue(permissionResult);
    }

    @Test
    public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserAdminPermission() throws NoSuchFieldException, IllegalAccessException {
        final User user = new User();
        user.setKeycloakId("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Map<User, PermissionCollection> userPermissionsMap = new HashMap<>();

        PermissionType adminPermission = PermissionType.ADMIN;
        PermissionCollection permissionCollection = buildPermissionCollection(PermissionCollectionType.ADMIN, adminPermission);
        userPermissionsMap.put(user, permissionCollection);
        // TODO Use service!
//        entityToCheck.setUserPermissions(userPermissionsMap);

        Set<PermissionType> allPermissions = new HashSet<>(Arrays.asList(PermissionType.values()));

        for (PermissionType permission : allPermissions) {
            boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertTrue(permissionResult);
        }
    }

    /**
     * Helper method to easily build a {@link PermissionCollection}
     *
     * @param permissions
     * @return
     */
    private PermissionCollection buildPermissionCollection(PermissionCollectionType permissionCollectionName, PermissionType... permissions) {
        return new PermissionCollection(new HashSet<>(Arrays.asList(permissions)), permissionCollectionName);
    }
}
