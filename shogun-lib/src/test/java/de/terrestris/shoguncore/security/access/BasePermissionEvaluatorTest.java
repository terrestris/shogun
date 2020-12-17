package de.terrestris.shoguncore.security.access;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.security.access.entity.ApplicationPermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.DefaultPermissionEvaluator;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

@RunWith(MockitoJUnitRunner.class)
public class BasePermissionEvaluatorTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private DefaultPermissionEvaluator defaultPermissionEvaluatorMock;

    @Mock
    private ApplicationPermissionEvaluator applicationPermissionEvaluatorMock;

    @Spy
    private ArrayList<BaseEntityPermissionEvaluator> baseEntityPermissionEvaluatorMock;

    @InjectMocks
    private BasePermissionEvaluator permissionEvaluator;

    private String mockUserMail = "test@shogun.de";
    private User mockUser;

    @Before
    public void setup() {
        mockUser = new User();
        mockUser.setEmail(mockUserMail);

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
    public void hasPermission_ShouldRestrictAccessIfNoUserIsAvailable() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("Not a User object");

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldHandleAnOptionalTargetEntity() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserMail);

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);

        when(userRepositoryMock.findOne(any())).thenReturn(Optional.of(mockUser));

        permissionEvaluator.hasPermission(authentication, Optional.of(targetDomainObject), permissionObject);

        verify(defaultPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userRepositoryMock);
        baseEntityPermissionEvaluatorMock.clear();
    }

    @Test
    public void hasPermission_ShouldCallTheDefaultPermissionEvaluatorIfNoExplicitImplementationIsAvailable() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserMail);

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);

        when(userRepositoryMock.findOne(any())).thenReturn(Optional.of(mockUser));

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(defaultPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userRepositoryMock);
        baseEntityPermissionEvaluatorMock.clear();
    }

    @Test
    public void hasPermission_ShouldCallTheAppropriatePermissionEvaluatorImplementation() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserMail);

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);
        baseEntityPermissionEvaluatorMock.add(applicationPermissionEvaluatorMock);

        when(userRepositoryMock.findOne(any())).thenReturn(Optional.of(mockUser));

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(defaultPermissionEvaluatorMock, times(0)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);
        verify(applicationPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userRepositoryMock);
    }

}
