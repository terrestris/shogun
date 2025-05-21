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

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.repository.security.permission.UserInstancePermissionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserInstancePermissionServiceSecuredTest extends BasePermissionServiceTest<UserInstancePermissionServiceSecured, UserInstancePermission> {

    @Mock
    UserInstancePermissionRepository repositoryMock;

    @InjectMocks
    UserInstancePermissionServiceSecured service;

    public void init() {
        super.setRepository(repositoryMock);
        super.setService(service);
        super.setEntityClass(UserInstancePermission.class);
    }

    @Test
    public void findFor_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_BaseEntity_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", BaseEntity.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_BaseEntity_PermissionCollectionType_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", BaseEntity.class, PermissionCollectionType.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findOwner_BaseEntity_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findOwner", BaseEntity.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void setPermission_BaseEntity_User_PermissionCollectionType_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("setPermission", BaseEntity.class, User.class, PermissionCollectionType.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void setPermission_List_User_PermissionCollectionType_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreFilter findAllPostFilter =
            service.getClass().getMethod("setPermission", List.class, User.class, PermissionCollectionType.class).getAnnotation(PreFilter.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void deleteFor_BaseEntity_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("deleteFor", BaseEntity.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

}
