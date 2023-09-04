/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2021-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.lib.controller.ApplicationController;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ApplicationControllerTest extends BaseControllerTest<ApplicationController, ApplicationRepository, Application> {

    public void setBaseEntity() {
        entityClass = Application.class;
    }

    public void setBasePath() {
        basePath = "/applications";
    }

    public void insertTestData() {
        Application entity1 = new Application();
        Application entity2 = new Application();
        Application entity3 = new Application();

        entity1.setName("Application 1");
        entity2.setName("Application 2");
        entity3.setName("Application 3");

        ArrayList<Application> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<Application> persistedEntities = (List<Application>) repository.saveAll(entities);

        testData = persistedEntities;
    }

    @Test
    public void findAll_shouldReturnFilteredEntitiesForAdminUsers() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath + "?filter=$.name == \"Application 1\"")
                    .with(authentication(getMockAuthentication(this.adminUser)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void findAll_shouldReturnFilteredEntitiesForUsersByUserInstancePermission() throws Exception {

        userInstancePermissionService.setPermission(testData.get(0), this.user, PermissionCollectionType.READ);
        userInstancePermissionService.setPermission(testData.get(1), this.user, PermissionCollectionType.READ);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath + "?filter=$.name == \"Application 1\"")
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void findAll_shouldReturnFilteredEntitiesForUsersByGroupInstancePermission() throws Exception {

        groupInstancePermissionService.setPermission(testData.get(0), this.group, PermissionCollectionType.READ);
        groupInstancePermissionService.setPermission(testData.get(1), this.group, PermissionCollectionType.READ);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath + "?filter=$.name == \"Application 1\"")
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
