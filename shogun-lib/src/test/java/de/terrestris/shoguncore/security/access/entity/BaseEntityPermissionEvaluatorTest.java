package de.terrestris.shoguncore.security.access.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.Identity;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.util.IdHelper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public abstract class BaseEntityPermissionEvaluatorTest<E extends BaseEntity> {

    @Mock
    private IdentityService identityService;

    private Class<E> entityClass;

    @InjectMocks
    private BaseEntityPermissionEvaluator<E> baseEntityPermissionEvaluator;

    private E entityToCheck;

    protected BaseEntityPermissionEvaluatorTest(Class<E> entityClass,
            BaseEntityPermissionEvaluator<E> baseEntityPermissionEvaluator,
            E entityToCheck) throws NoSuchFieldException {
        this.entityClass = entityClass;
        this.baseEntityPermissionEvaluator = baseEntityPermissionEvaluator;
        this.entityToCheck = entityToCheck;

        IdHelper.setIdForEntity(this.entityToCheck, 123L);
    }

    @Test
    public void hasPermission_shouldNeverGrantAnythingWithoutPermissions() throws NoSuchFieldException {
        final User user = new User();
        user.setUsername("Test user");
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
        user.setUsername("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Map<User, PermissionCollection> userPermissionsMap = new HashMap<>();

        PermissionType updatePermission = PermissionType.UPDATE;
        PermissionCollection permissionCollection = buildPermissionCollection(updatePermission);
        userPermissionsMap.put(user, permissionCollection);
        // TODO Use service!
//        entityToCheck.setUserPermissions(userPermissionsMap);

        boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, updatePermission);

        assertTrue(permissionResult);
    }

    @Test
    public void hasPermission_shouldGrantPermissionOnSecuredObjectWithCorrectGroupPermission() throws NoSuchFieldException, IllegalAccessException {
        final User user = new User();
        user.setUsername("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Group group = new Group();
        group.setName("Test group");

        Identity identity = new Identity();
        identity.setUser(user);
        identity.setGroup(group);
        // TODO Check if needed
//        identity.setRole();

        Map<Group, PermissionCollection> groupPermissionsMap = new HashMap<>();

        PermissionType updatePermission = PermissionType.UPDATE;
        PermissionCollection permissionCollection = buildPermissionCollection(updatePermission);
        groupPermissionsMap.put(group, permissionCollection);
        // TODO Rest permissions from previous test?!
        // TODO Use service!
//        entityToCheck.setGroupPermissions(groupPermissionsMap);

        when(identityService.findAllMembersOf(group)).thenReturn(Arrays.asList(user));

        boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, updatePermission);

        assertTrue(permissionResult);
    }

    @Test
    public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserAdminPermission() throws NoSuchFieldException, IllegalAccessException {
        final User user = new User();
        user.setUsername("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Map<User, PermissionCollection> userPermissionsMap = new HashMap<>();

        PermissionType adminPermission = PermissionType.ADMIN;
        PermissionCollection permissionCollection = buildPermissionCollection(adminPermission);
        userPermissionsMap.put(user, permissionCollection);
        // TODO Use service!
//        entityToCheck.setUserPermissions(userPermissionsMap);

        Set<PermissionType> allPermissions = new HashSet<>(Arrays.asList(PermissionType.values()));

        for (PermissionType permission : allPermissions) {
            boolean permissionResult = baseEntityPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertTrue(permissionResult);
        }
    }

    @Test
    public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserGroupAdminPermission() throws NoSuchFieldException, IllegalAccessException {
        final User user = new User();
        user.setUsername("Test user");
        IdHelper.setIdForEntity(user, 1909L);

        Group group = new Group();
        group.setName("Test group");

        Identity identity = new Identity();
        identity.setUser(user);
        identity.setGroup(group);
        // TODO Check if needed
//        identity.setRole();

        Map<Group, PermissionCollection> groupPermissionsMap = new HashMap<>();

        PermissionType adminPermission = PermissionType.ADMIN;
        PermissionCollection permissionCollection = buildPermissionCollection(adminPermission);
        groupPermissionsMap.put(group, permissionCollection);
        // TODO Use service!
//        entityToCheck.setGroupPermissions(groupPermissionsMap);

        when(identityService.findAllMembersOf(group)).thenReturn(Arrays.asList(user));

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
    private PermissionCollection buildPermissionCollection(PermissionType... permissions) {
        return new PermissionCollection(new HashSet<>(Arrays.asList(permissions)));
    }
}
