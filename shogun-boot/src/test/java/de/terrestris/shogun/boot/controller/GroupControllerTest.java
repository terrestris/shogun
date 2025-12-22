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
import de.terrestris.shogun.lib.controller.GroupController;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

public class GroupControllerTest extends BaseControllerTest<GroupController, GroupRepository, Group> {

    public void setBaseEntity() {
        entityClass = Group.class;
    }

    public void setBasePath() {
        basePath = "/groups";
    }

    public void insertTestData() {
        Group entity1 = new Group();
        Group entity2 = new Group();
        Group entity3 = new Group();

        entity1.setAuthProviderId(UUID.randomUUID().toString());
        entity2.setAuthProviderId(UUID.randomUUID().toString());
        entity3.setAuthProviderId(UUID.randomUUID().toString());

        ArrayList<Group> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        testData = (List<Group>) repository.saveAll(entities);
    }

    @Test
    @Override
    public void findOne_shouldDenyAccessForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
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

        List<Group> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
    @Override
    public void update_shouldDenyAccessForRoleAnonymous() throws Exception {
        JsonNode updateNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("created", "modified");
        updateNode = ((ObjectNode) updateNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Charset.defaultCharset())
                    .content(objectMapper.writeValueAsString(updateNode))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
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
                    .characterEncoding(Charset.defaultCharset())
                    .content(objectMapper.writeValueAsString(insertNode))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        List<Group> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
    public void findAll_shouldDenyAccessForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @Override
    public void findAll_shouldReturnOnlyPublicEntitiesForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @Override
    public void post_permission_public_shouldAddPublicReadPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(String.format("%s/%s/permissions/public", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Override
    public void delete_permission_public_shouldRemovePublicReadPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s/permissions/public", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Override
    public void get_permission_public_shouldReturnPublicReadPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s/permissions/public", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}
