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

import de.terrestris.shogun.lib.model.security.permission.UserInstancePermission;
import de.terrestris.shogun.lib.repository.security.permission.UserInstancePermissionRepository;
import de.terrestris.shogun.lib.service.BaseServiceTest;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserInstancePermissionServiceTest extends BaseServiceTest<UserInstancePermissionService, UserInstancePermission> {

    @Mock
    UserInstancePermissionRepository repositoryMock;

    @InjectMocks
    UserInstancePermissionService service;

    @Before
    public void init() {
        super.setRepository(repositoryMock);
        super.setService(service);
        super.setEntityClass(UserInstancePermission.class);
    }

}
