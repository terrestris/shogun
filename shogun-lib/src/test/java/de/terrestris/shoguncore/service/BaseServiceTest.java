package de.terrestris.shoguncore.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseServiceTest<U extends BaseService, S extends BaseEntity> implements IBaseServiceTest {

    protected Class<S> entityClass;

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    ObjectReader objectReaderMock;

    @Mock
    private BaseCrudRepository baseCrudRepositoryMock;

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
    public void create_ShouldCallCorrectRepositoryMethodAndShouldReturnTheCreatedEntityOfGivenBaseEntity() {
        S mockEntity = mock(entityClass, CALLS_REAL_METHODS);
        mockEntity.setId(1909L);

        S entityToSave = mock(entityClass);

        when(baseCrudRepositoryMock.save(entityToSave)).thenReturn(mockEntity);

        S returnValue = (S) service.create(entityToSave);

        verify(baseCrudRepositoryMock, times(1)).save(entityToSave);
        assertNotEquals(returnValue.getId(), entityToSave.getId());
    }

    @Test
    public void update_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize udpatePreAuthorize =
            service.getClass().getMethod("update", Long.class, BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(udpatePreAuthorize);
        assertTrue(udpatePreAuthorize.value().contains("hasRole"));
        assertTrue(udpatePreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void update_ShouldCallCorrectRepositoryMethodAndShouldReturnTheCreatedEntityOfGivenBaseEntity() throws IOException {
        S mockEntity = mock(entityClass, CALLS_REAL_METHODS);
        Date date = new Date();
        mockEntity.setId(1909L);
        mockEntity.setCreated(date);
        mockEntity.setModified(date);

        JsonNode returnNode = JsonNodeFactory.instance.objectNode();
        ((ObjectNode) returnNode).put("id", 1909);
        ((ObjectNode) returnNode).put("created", date.toString());
        ((ObjectNode) returnNode).put("modified", date.toString());

        when(baseCrudRepositoryMock.findById(1909L)).thenReturn(Optional.of(mockEntity));
        when(baseCrudRepositoryMock.save(mockEntity)).thenReturn(mockEntity);

        when(objectMapperMock.valueToTree(mockEntity)).thenReturn(returnNode);
        when(objectMapperMock.readerForUpdating(mockEntity)).thenReturn(objectReaderMock);
        when(objectReaderMock.readValue(returnNode)).thenReturn(mockEntity);

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

    protected void setEntityClass(Class<S> entityClass) {
        this.entityClass = entityClass;
    }
}
