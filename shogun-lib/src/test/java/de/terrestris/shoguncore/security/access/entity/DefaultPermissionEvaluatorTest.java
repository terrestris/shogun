package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.PermissionCollection;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.service.security.permission.GroupClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shoguncore.service.security.permission.UserClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPermissionEvaluatorTest {

    @Mock
    private IdentityService identityService;

    @Mock
    private UserInstancePermissionService userInstancePermissionService;

    @Mock
    private GroupInstancePermissionService groupInstancePermissionService;

    @Mock
    private UserClassPermissionService userClassPermissionService;

    @Mock
    private GroupClassPermissionService groupClassPermissionService;

    @InjectMocks
    private DefaultPermissionEvaluator defaultPermissionEvaluator;

    private String mockUserMail = "test@shogun.de";
    private User mockUser;

    @Before
    public void setup() {
        mockUser = new User();
        mockUser.setEmail(mockUserMail);
    }

    @Test
    public void hasPermission_shouldNeverGrantAnythingWithoutPermissions() {
        BaseEntity entityToCheck = mock(BaseEntity.class);

        Set<PermissionType> allPermissions = new HashSet<>(Arrays.asList(PermissionType.values()));

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());

        for (PermissionType permission : allPermissions) {
            boolean permissionResult = defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, permission);

            assertFalse(permissionResult);
        }

        resetMocks();
    }

    @Test
    public void hasPermission_shouldEvaluatePermissionsInLogicalOrder() {
        BaseEntity entityToCheck = mock(BaseEntity.class);

        PermissionType readPermission = PermissionType.READ;

        PermissionCollection permissionCollection = buildPermissionCollection(readPermission);

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, readPermission));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, readPermission));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, readPermission));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, readPermission));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();
    }

    @Test
    public void hasPermission_shouldGrantReadPermissionByUserInstancePermission() {
        BaseEntity entityToCheck = mock(BaseEntity.class);

        PermissionType readPermission = PermissionType.READ;

        PermissionCollection permissionCollection = buildPermissionCollection(readPermission);

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, readPermission));

        resetMocks();
    }

    private void resetMocks() {
        reset(userInstancePermissionService);
        reset(groupInstancePermissionService);
        reset(userClassPermissionService);
        reset(groupClassPermissionService);
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
