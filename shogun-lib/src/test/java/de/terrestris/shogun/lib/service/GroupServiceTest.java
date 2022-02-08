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

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.service.security.provider.keycloak.KeycloakGroupProviderService;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GroupServiceTest extends BaseServiceTest<GroupService, Group> {

    @Mock
    GroupRepository repositoryMock;

    @Mock
    KeycloakUtil keycloakUtilMock;

    @InjectMocks
    GroupService service;

    @InjectMocks
    KeycloakGroupProviderService groupProviderService;

    @Before
    public void init() {
        when(keycloakUtilMock.getGroupResource(any(Group.class))).thenReturn(null);
        service.groupProviderService = groupProviderService;

        super.setRepository(repositoryMock);
        super.setService(service);
        super.setEntityClass(Group.class);
    }

}
