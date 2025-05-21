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
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.GroupClassPermission;
import de.terrestris.shogun.lib.repository.security.permission.GroupClassPermissionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupClassPermissionServiceSecuredTest extends BasePermissionServiceTest<GroupClassPermissionServiceSecured, GroupClassPermission> {

    @Mock
    GroupClassPermissionRepository repositoryMock;

    @InjectMocks
    GroupClassPermissionServiceSecured service;

    public void init() {
        super.setRepository(repositoryMock);
        super.setService(service);
        super.setEntityClass(GroupClassPermission.class);
    }

    @Test
    public void findFor_Group_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", Group.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_BaseEntity_Group_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", BaseEntity.class, Group.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_Class_Group_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", Class.class, Group.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_Class_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", Class.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findFor_BaseEntity_Group_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findAllPostFilter =
            service.getClass().getMethod("findFor", BaseEntity.class, Group.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findAllPostFilter);
        assertTrue(findAllPostFilter.value().contains("hasRole"));
        assertTrue(findAllPostFilter.value().contains("hasPermission"));
    }

    @Test
    public void findPermissionCollectionFor_BaseEntity_Group_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findOnePostAuthorize =
            service.getClass().getMethod("findPermissionCollectionFor", BaseEntity.class, Group.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findOnePostAuthorize);
        assertTrue(findOnePostAuthorize.value().contains("hasRole"));
        assertTrue(findOnePostAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void findPermissionCollectionFor_BaseEntity_Group_User_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize findOnePostAuthorize =
            service.getClass().getMethod("findPermissionCollectionFor", BaseEntity.class, Group.class, User.class).getAnnotation(PreAuthorize.class);

        assertNotNull(findOnePostAuthorize);
        assertTrue(findOnePostAuthorize.value().contains("hasRole"));
        assertTrue(findOnePostAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void setPermission_Class_Group_PermissionCollectionType_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize createPreAuthorize =
            service.getClass().getMethod("setPermission", Class.class, Group.class, PermissionCollectionType.class).getAnnotation(PreAuthorize.class);

        assertNotNull(createPreAuthorize);
        assertTrue(createPreAuthorize.value().contains("hasRole"));
        assertTrue(createPreAuthorize.value().contains("hasPermission"));
    }

    @Test
    public void deleteFor_BaseEntity_Group_IsAnnotatedAsExpected() throws NoSuchMethodException {
        PreAuthorize deletePreAuthorize =
            service.getClass().getMethod("deleteFor", BaseEntity.class, Group.class).getAnnotation(PreAuthorize.class);

        assertNotNull(deletePreAuthorize);
        assertTrue(deletePreAuthorize.value().contains("hasRole"));
        assertTrue(deletePreAuthorize.value().contains("hasPermission"));
    }

}
