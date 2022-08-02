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
import de.terrestris.shogun.lib.model.security.permission.BasePermission;
import de.terrestris.shogun.lib.repository.security.permission.BasePermissionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
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

    @Test
    public void class_isAnnotatedAsService() {
        assertNotNull(service.getClass().getAnnotation(Service.class));
    }

    @Test
    public void findAll_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PostFilter findAllPostFilter =
            service.getClass().getMethod("findAll").getAnnotation(PostFilter.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findOne_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PostAuthorize findOnePostAuthorize =
            service.getClass().getMethod("findOne", Long.class).getAnnotation(PostAuthorize.class);

        assertNotNull(findOnePostAuthorize);
        assertTrue(findOnePostAuthorize.value().contains("hasRole"));
        assertTrue(findOnePostAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void create_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize createPreAuthorize =
            service.getClass().getMethod("create", BasePermission.class).getAnnotation(PreAuthorize.class);

        assertNotNull(createPreAuthorize);
        assertTrue(createPreAuthorize.value().contains("hasRole"));
        assertTrue(createPreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void update_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize updatePreAuthorize =
            service.getClass().getMethod("update", Long.class, BasePermission.class).getAnnotation(PreAuthorize.class);

        assertNotNull(updatePreAuthorize);
        assertTrue(updatePreAuthorize.value().contains("hasRole"));
        assertTrue(updatePreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void delete_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize deletePreAuthorize =
            service.getClass().getMethod("delete", BasePermission.class).getAnnotation(PreAuthorize.class);

        assertNotNull(deletePreAuthorize);
        assertTrue(deletePreAuthorize.value().contains("hasRole"));
        assertTrue(deletePreAuthorize.value().contains("hasPermission"));
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
