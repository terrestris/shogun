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
package de.terrestris.shogun.lib.security.access;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.access.entity.ApplicationPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import de.terrestris.shogun.lib.service.security.provider.keycloak.KeycloakUserProviderService;
import de.terrestris.shogun.lib.util.IdHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasePermissionEvaluatorTest {

    @Mock
    private DefaultPermissionEvaluator defaultPermissionEvaluatorMock;

    @Mock
    private ApplicationPermissionEvaluator applicationPermissionEvaluatorMock;

    @Mock
    private UserProviderService userProviderService = new KeycloakUserProviderService();

    @Spy
    private ArrayList<BaseEntityPermissionEvaluator> baseEntityPermissionEvaluatorMock;

    @InjectMocks
    private BasePermissionEvaluator permissionEvaluator;

    private String mockUserKeycloakId = "bf5efad6-50f5-448c-b808-60dc0259d70b";
    private User mockUser;

    @Before
    public void setup() {
        mockUser = new User();
        mockUser.setKeycloakId(mockUserKeycloakId);

        when(defaultPermissionEvaluatorMock.getEntityClassName()).thenReturn(BaseEntity.class);
        when(applicationPermissionEvaluatorMock.getEntityClassName()).thenReturn(Application.class);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfAuthenticationIsNull() {
        Authentication authentication = null;
        Application targetDomainObject = new Application();
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfTargetDomainObjectIsNull() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = null;
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfPermissionObjectIsNull() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = new Application();
        String permissionObject = null;

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfPermissionObjectIsNotAString() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = new Application();
        int permissionObject = 42;

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldHandleAnOptionalTargetEntity() throws NoSuchFieldException {
        Authentication authentication = mock(Authentication.class);

        Application targetDomainObject = new Application();
        IdHelper.setIdForEntity(targetDomainObject, 1L);
        String permissionObject = "READ";

        when(userProviderService.getUserFromAuthentication(authentication)).thenReturn(Optional.of(mockUser));

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);

        permissionEvaluator.hasPermission(authentication, Optional.of(targetDomainObject), permissionObject);

        verify(defaultPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userProviderService);
        baseEntityPermissionEvaluatorMock.clear();
    }

    @Test
    public void hasPermission_ShouldCallTheDefaultPermissionEvaluatorIfNoExplicitImplementationIsAvailable() throws NoSuchFieldException {
        Authentication authentication = mock(Authentication.class);

        Application targetDomainObject = new Application();
        IdHelper.setIdForEntity(targetDomainObject, 1L);
        String permissionObject = "READ";

        when(userProviderService.getUserFromAuthentication(authentication)).thenReturn(Optional.of(mockUser));

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(defaultPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userProviderService);
        baseEntityPermissionEvaluatorMock.clear();
    }

    @Test
    public void hasPermission_ShouldCallTheAppropriatePermissionEvaluatorImplementation() throws NoSuchFieldException {
        Authentication authentication = mock(Authentication.class);

        Application targetDomainObject = new Application();
        IdHelper.setIdForEntity(targetDomainObject, 1L);
        String permissionObject = "READ";

        when(userProviderService.getUserFromAuthentication(authentication)).thenReturn(Optional.of(mockUser));

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);
        baseEntityPermissionEvaluatorMock.add(applicationPermissionEvaluatorMock);

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(defaultPermissionEvaluatorMock, times(0)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);
        verify(applicationPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userProviderService);
    }

}
