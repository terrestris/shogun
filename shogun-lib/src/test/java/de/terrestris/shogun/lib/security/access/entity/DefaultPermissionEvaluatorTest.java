/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.service.security.permission.*;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultPermissionEvaluatorTest {

    @Mock
    private UserInstancePermissionService userInstancePermissionService;

    @Mock
    private GroupInstancePermissionService groupInstancePermissionService;

    @Mock
    private UserClassPermissionService userClassPermissionService;

    @Mock
    private GroupClassPermissionService groupClassPermissionService;

    @Mock
    private RoleProviderService roleProviderService;

    @Mock
    private PublicInstancePermissionService publicInstancePermissionService;

    @InjectMocks
    private DefaultPermissionEvaluator defaultPermissionEvaluator;

    private String mockUserKeycloakId = "bf5efad6-50f5-448c-b808-60dc0259d70b";
    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setAuthProviderId(mockUserKeycloakId);
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

        PermissionCollectionType readPermission = PermissionCollectionType.READ;

        PermissionCollection permissionCollection = buildPermissionCollection(readPermission);

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, PermissionType.READ));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, PermissionType.READ));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, PermissionType.READ));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(0)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, PermissionType.READ));

        verify(userInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupInstancePermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(userClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);
        verify(groupClassPermissionService, times(1)).findPermissionCollectionFor(entityToCheck, mockUser);

        resetMocks();
    }

    @Test
    public void hasPermission_shouldGrantReadPermissionByUserInstancePermission() {
        BaseEntity entityToCheck = mock(BaseEntity.class);

        PermissionCollectionType readPermission = PermissionCollectionType.READ;

        PermissionCollection permissionCollection = buildPermissionCollection(readPermission);

        when(userInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(permissionCollection);
        when(groupInstancePermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(userClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());
        when(groupClassPermissionService.findPermissionCollectionFor(entityToCheck, mockUser)).thenReturn(new PermissionCollection());

        assertTrue(defaultPermissionEvaluator.hasPermission(mockUser, entityToCheck, PermissionType.READ));

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
     * @param permissionCollectionType
     * @return
     */
    private PermissionCollection buildPermissionCollection(PermissionCollectionType permissionCollectionType) {

        Set<PermissionType> permissions = Arrays.stream(permissionCollectionType.toString().split("_"))
            .map(permission -> PermissionType.valueOf(permission))
            .collect(Collectors.toSet());

        return new PermissionCollection(permissions, permissionCollectionType);
    }

}
