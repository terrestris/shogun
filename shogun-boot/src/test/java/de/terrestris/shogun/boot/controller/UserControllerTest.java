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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terrestris.shogun.lib.controller.UserController;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UserControllerTest extends BaseControllerTest<UserController, UserRepository, User> {

    public void setBaseEntity () {
        entityClass = User.class;
    };

    public void setBasePath() {
        basePath = "/users";
    }

    public void insertTestData() {
        User entity1 = new User();
        User entity2 = new User();
        User entity3 = new User();

        entity1.setAuthProviderId(UUID.randomUUID().toString());
        entity2.setAuthProviderId(UUID.randomUUID().toString());
        entity3.setAuthProviderId(UUID.randomUUID().toString());

        ArrayList<User> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<User> persistedEntities = (List<User>) repository.saveAll(entities);

        testData = persistedEntities;
    }

    @Test
    @Override
    public void findAll_shouldReturnAllAvailableEntitiesForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(authentication(getMockAuthentication(this.adminUser)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(testData.size() + 2)));
    }

    @Test
    @Override
    public void add_shouldDenyAccessForRoleAnonymous() throws Exception {
        JsonNode insertNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("id", "created", "modified");
        insertNode = ((ObjectNode) insertNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(String.format("%s", basePath))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(insertNode))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        List<User> persistedEntities = repository.findAll();
        assertEquals(5, persistedEntities.size());
    }

    @Test
    @Override
    public void add_shouldDenyAccessForRoleUserWithoutExplicitPermission() throws Exception {
        JsonNode insertNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("id", "created", "modified");
        insertNode = ((ObjectNode) insertNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(String.format("%s", basePath))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(insertNode))
                    .with(authentication(getMockAuthentication(this.user)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        List<User> persistedEntities = repository.findAll();
        assertEquals(5, persistedEntities.size());
    }

    @Test
    @Override
    public void add_shouldCreateTheEntityForRoleUser() throws Exception {

        userClassPermissionService.setPermission(entityClass, this.user, PermissionCollectionType.CREATE);

        JsonNode insertNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("id", "created", "modified");
        insertNode = ((ObjectNode) insertNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(String.format("%s", basePath))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(insertNode))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasKey("id")));

        List<User> persistedEntities = repository.findAll();
        assertEquals(6, persistedEntities.size());
    }

    @Test
    @Override
    public void add_shouldCreateTheEntityForRoleAdmin() throws Exception {
        JsonNode insertNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("id", "created", "modified");
        insertNode = ((ObjectNode) insertNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(String.format("%s", basePath))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(insertNode))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasKey("id")));

        List<User> persistedEntities = repository.findAll();
        assertEquals(6, persistedEntities.size());
    }

    @Test
    @Override
    public void delete_shouldDenyAccessForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        List<User> persistedEntities = repository.findAll();
        assertEquals(5, persistedEntities.size());
    }

    @Test
    @Override
    public void delete_shouldDenyAccessForRoleUserWithoutExplicitPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.user)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        List<User> persistedEntities = repository.findAll();
        assertEquals(5, persistedEntities.size());
    }

    @Test
    @Override
    public void delete_shouldDeleteAnAvailableEntityForRoleUser() throws Exception {

        userInstancePermissionService.setPermission(testData.get(0), this.user, PermissionCollectionType.DELETE);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<User> persistedEntities = repository.findAll();
        assertEquals(4, persistedEntities.size());
    }

    @Test
    @Override
    public void delete_shouldDeleteAnAvailableEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<User> persistedEntities = repository.findAll();
        assertEquals(4, persistedEntities.size());
    }
}
