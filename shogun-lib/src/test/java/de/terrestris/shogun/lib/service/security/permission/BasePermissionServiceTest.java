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
package de.terrestris.shogun.lib.service.security.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.BasePermission;
import de.terrestris.shogun.lib.repository.security.permission.BasePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public abstract class BasePermissionServiceTest<U extends BasePermissionService, S extends BasePermission> implements IBasePermissionServiceTest {

    protected Class<S> entityClass;

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    ObjectReader objectReaderMock;

    @Mock
    private UserInstancePermissionService userInstancePermissionServiceMock;

    @Mock
    private GroupInstancePermissionService groupInstancePermissionServiceMock;

    private BasePermissionRepository basePermissionRepositoryMock;

    protected U service;

    @BeforeEach
    public void callInit() {
        init();
    }

    @Test
    public void class_isAnnotatedAsService() {
        assertNotNull(service.getClass().getAnnotation(Service.class));
    }

    @Test
    public void findFor_BaseEntity_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findPermissionCollectionFor_BaseEntity_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findOnePostAuthorize =
            service.getClass().getMethod("findPermissionCollectionFor", BaseEntity.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findOnePostAuthorize);
        assertTrue(findOnePostAuthorize.value().contains("hasRole"));
        assertTrue(findOnePostAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void deleteAllFor_BaseEntity_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize updatePreAuthorize =
            service.getClass().getMethod("deleteAllFor", BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(updatePreAuthorize);
        assertTrue(updatePreAuthorize.value().contains("hasRole"));
        assertTrue(updatePreAuthorize.value().contains("hasPermission"));
    }

    protected void setService(U service) {
        this.service = service;
    }

    protected void setRepository(BasePermissionRepository repository) {
        this.basePermissionRepositoryMock = repository;
    }

    protected void setEntityClass(Class<S> entityClass) {
        this.entityClass = entityClass;
    }
}
