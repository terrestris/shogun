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
package de.terrestris.shogun.lib.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.service.security.permission.*;
import de.terrestris.shogun.lib.service.security.provider.GroupProviderService;
import de.terrestris.shogun.lib.service.security.provider.RoleProviderService;
import de.terrestris.shogun.lib.service.security.provider.UserProviderService;
import de.terrestris.shogun.lib.util.IdHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest<U extends BaseService, S extends BaseEntity> implements IBaseServiceTest {

    protected Class<S> entityClass;

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    ObjectReader objectReaderMock;

    @Mock
    private UserInstancePermissionService userInstancePermissionServiceMock;

    @Mock
    private GroupInstancePermissionService groupInstancePermissionServiceMock;

    @Mock
    private RoleInstancePermissionService roleInstancePermissionServiceMock;

    @Mock
    private UserProviderService userProviderService;

    @Mock
    private GroupProviderService GroupProviderService;

    @Mock
    private RoleProviderService roleProviderService;

    @Mock
    private UserClassPermissionService userClassPermissionService;

    @Mock
    private GroupClassPermissionService groupClassPermissionService;

    @Mock
    private RoleClassPermissionService roleClassPermissionService;

    private BaseCrudRepository baseCrudRepositoryMock;

    protected U service;

    @BeforeEach
    public void callInit() {
        init();

        // For some unknown reason Mockito can't inject the repository mock,
        // very probably due to the fact that the repository is of generic type.
        // See also https://github.com/mockito/mockito/issues/3207
        try {
            Field field = BaseService.class.getDeclaredField("repository");
            field.setAccessible(true);
            field.set(service, baseCrudRepositoryMock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
    public void findAll_ShouldCallCorrectRepositoryMethodAndShouldReturnListOfGivenBaseEntity() {
        S mockEntity1 = mock(entityClass);
        S mockEntity2 = mock(entityClass);
        S mockEntity3 = mock(entityClass);

        ArrayList<S> entityList = new ArrayList<>();

        entityList.add(mockEntity1);
        entityList.add(mockEntity2);
        entityList.add(mockEntity3);

        when(baseCrudRepositoryMock.findAll()).thenReturn(entityList);

        List returnValue = service.findAll();

        verify(baseCrudRepositoryMock, times(1)).findAll();
        assertEquals(returnValue, entityList);
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
    public void findOne_ShouldCallCorrectRepositoryMethodAndShouldReturnSingleResultOfGivenBaseEntity() {
        Optional<S> mockEntity = Optional.of(mock(entityClass));

        when(baseCrudRepositoryMock.findById(1909L)).thenReturn(mockEntity);

        Optional returnValue = service.findOne(1909L);

        verify(baseCrudRepositoryMock, times(1)).findById(1909L);
        assertEquals(returnValue, mockEntity);

        Optional anotherReturnValue = service.findOne(1904L);

        verify(baseCrudRepositoryMock, times(1)).findById(1904L);
        assertNotEquals(anotherReturnValue, mockEntity);
    }

    @Test
    public void create_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize createPreAuthorize =
            service.getClass().getMethod("create", BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(createPreAuthorize);
        assertTrue(createPreAuthorize.value().contains("hasRole"));
        assertTrue(createPreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void create_ShouldCallCorrectRepositoryMethodAndShouldReturnTheCreatedEntityOfGivenBaseEntity() throws NoSuchFieldException {
        when(userProviderService.getUserBySession()).thenReturn(Optional.of(new User()));

        S mockEntity = mock(entityClass, CALLS_REAL_METHODS);
        IdHelper.setIdForEntity(mockEntity, 1909L);

        S entityToSave = mock(entityClass);

        when(baseCrudRepositoryMock.save(entityToSave)).thenReturn(mockEntity);

        S returnValue = (S) service.create(entityToSave);

        verify(baseCrudRepositoryMock, times(1)).save(entityToSave);
        assertNotEquals(returnValue.getId(), entityToSave.getId());
    }

    @Test
    public void update_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize updatePreAuthorize =
            service.getClass().getMethod("update", Long.class, BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(updatePreAuthorize);
        assertTrue(updatePreAuthorize.value().contains("hasRole"));
        assertTrue(updatePreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void update_ShouldCallCorrectRepositoryMethodAndShouldReturnTheCreatedEntityOfGivenBaseEntity() throws IOException, NoSuchFieldException {
        S mockEntity = mock(entityClass, CALLS_REAL_METHODS);
        OffsetDateTime date = OffsetDateTime.now();
        IdHelper.setIdForEntity(mockEntity, 1909L);
        mockEntity.setCreated(date);
        mockEntity.setModified(date);

        JsonNode returnNode = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) returnNode).put("id", 1909);
        ((ObjectNode) returnNode).put("created", date.toString());
        ((ObjectNode) returnNode).put("modified", date.toString());

        when(baseCrudRepositoryMock.findById(1909L)).thenReturn(Optional.of(mockEntity));
        when(baseCrudRepositoryMock.save(mockEntity)).thenReturn(mockEntity);

        S returnValue = (S) service.update(1909L, mockEntity);

        verify(baseCrudRepositoryMock, times(1)).findById(1909L);
        verify(baseCrudRepositoryMock, times(1)).save(mockEntity);
        assertEquals(returnValue, mockEntity);
    }

    @Test
    public void delete_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize deletePreAuthorize =
            service.getClass().getMethod("delete", BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(deletePreAuthorize);
        assertTrue(deletePreAuthorize.value().contains("hasRole"));
        assertTrue(deletePreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void delete_ShouldCallCorrectRepositoryMethod() {
        S mockEntity = mock(entityClass);

        service.delete(mockEntity);

        verify(baseCrudRepositoryMock, times(1)).delete(mockEntity);
    }

    protected void setService(U service) {
        this.service = service;
    }

    protected void setRepository(BaseCrudRepository repository) {
        this.baseCrudRepositoryMock = repository;
    }

    protected void setEntityClass(Class<S> entityClass) {
        this.entityClass = entityClass;
    }
}
