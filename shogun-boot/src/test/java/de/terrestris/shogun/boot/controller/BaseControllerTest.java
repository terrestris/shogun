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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terrestris.shogun.boot.config.ApplicationConfig;
import de.terrestris.shogun.boot.config.JdbcConfiguration;
import de.terrestris.shogun.lib.controller.BaseController;
import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.BaseCrudRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserClassPermissionRepository;
import de.terrestris.shogun.lib.repository.security.permission.UserInstancePermissionRepository;
import de.terrestris.shogun.lib.service.security.permission.UserClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(
    classes = {
        ApplicationConfig.class,
        JdbcConfiguration.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public abstract class BaseControllerTest<U extends BaseController, R extends BaseCrudRepository, S extends BaseEntity> implements IBaseController {

    @Autowired
    protected R repository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserInstancePermissionRepository userInstancePermissionRepository;

    @Autowired
    protected UserClassPermissionRepository userClassPermissionRepository;

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    protected UserClassPermissionService userClassPermissionService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Class<S> entityClass;

    protected String basePath;

    protected MockMvc mockMvc;

    protected List<S> testData;

    protected User adminUser;

    protected User user;

    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .alwaysDo(print())
            .apply(springSecurity())
            .build();
    }

    public void initAdminUser() {
        User<UserRepresentation> adminUser = new User();
        String keycloakId = "bf5efad6-50f5-448c-b808-60dc0259d70b";
        adminUser.setAuthProviderId(keycloakId);
        UserRepresentation keycloakRepresentation = new UserRepresentation();
        keycloakRepresentation.setEmail("admin@shogun.de");
        keycloakRepresentation.setEnabled(true);
        keycloakRepresentation.setUsername("admin");
        ArrayList<String> realmRoles = new ArrayList<>();
        realmRoles.add("ROLE_ADMIN");
        keycloakRepresentation.setRealmRoles(realmRoles);
        adminUser.setProviderDetails(keycloakRepresentation);

        this.adminUser = userRepository.save(adminUser);
    }

    public void initUser() {
        User<UserRepresentation> user = new User();
        String keycloakId = "01e680f5-e8a4-460f-8897-12b4cf893739";
        user.setAuthProviderId(keycloakId);
        UserRepresentation keycloakRepresentation = new UserRepresentation();
        keycloakRepresentation.setEmail("user@shogun.de");
        keycloakRepresentation.setEnabled(true);
        keycloakRepresentation.setUsername("user");
        ArrayList<String> realmRoles = new ArrayList<>();
        realmRoles.add("ROLE_USER");
        keycloakRepresentation.setRealmRoles(realmRoles);
        user.setProviderDetails(keycloakRepresentation);

        this.user = userRepository.save(user);
    }

    public void cleanupPermissions() {
        userInstancePermissionRepository.deleteAll();
        userClassPermissionRepository.deleteAll();
    }

    public void deinitAdminUser() {
        userRepository.delete(this.adminUser);
    }

    public void deinitUser() {
        userRepository.delete(this.user);
    }

    public void cleanupTestData() {
        repository.deleteAll();
    }

    public JwtAuthenticationToken getMockAuthentication(User<UserRepresentation> mockUser) {
        IDToken idToken = new IDToken();
        idToken.setSubject(mockUser.getAuthProviderId());

        Set<String> roles = new HashSet<>(mockUser.getProviderDetails().getRealmRoles());

        Set<SimpleGrantedAuthority> authorities = mockUser.getProviderDetails().getRealmRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toSet());

        Jwt jwt = new Jwt(
            "dummyToken",
            Instant.now(),
            Instant.now().plusSeconds(30),
            Map.of("alg", "none"),
            Map.of ("sub", mockUser.getAuthProviderId())
        );

        return new JwtAuthenticationToken(jwt, authorities);
    }

    @BeforeEach
    public void setUp() {
        initMockMvc();
        initAdminUser();
        initUser();
        setBaseEntity();
        setBasePath();
        insertTestData();
    }

    @AfterEach
    public void teardown() {
        cleanupPermissions();
        deinitUser();
        deinitAdminUser();
        cleanupTestData();
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
    public void findAll_shouldReturnNoEntitiesForRoleUserWithoutExplicitPermissions() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    public void findAll_shouldReturnAllAvailableEntitiesForRoleUser() throws Exception {

        userInstancePermissionService.setPermission(this.testData.get(0), this.user, PermissionCollectionType.READ);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void findAll_shouldReturnAllAvailableEntitiesWithUserClassPermissions() throws Exception {

        userClassPermissionService.setPermission(entityClass, this.user, PermissionCollectionType.READ);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(testData.size())));
    }

    @Test
    public void findAll_shouldReturnAllAvailableEntitiesForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(authentication(getMockAuthentication(this.adminUser)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(testData.size())));
    }

    @Test
    public void findOne_shouldDenyAccessForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void findOne_shouldReturnNoEntityForRoleUserWithoutExplicitPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findOne_shouldReturnAnAvailableEntityForRoleUser() throws Exception {

        userInstancePermissionService.setPermission(this.testData.get(0), this.user, PermissionCollectionType.READ);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.user)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasEntry("id", testData.get(0).getId().intValue())));
    }

    @Test
    public void findOne_shouldReturnAnAvailableEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasEntry("id", testData.get(0).getId().intValue())));
    }

    @Test
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

        List<S> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
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

        List<S> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
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

        List<S> persistedEntities = repository.findAll();
        assertEquals(4, persistedEntities.size());
    }

    @Test
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

        List<S> persistedEntities = repository.findAll();
        assertEquals(4, persistedEntities.size());
    }

    @Test
    public void update_shouldDenyAccessForRoleAnonymous() throws Exception {
        JsonNode updateNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("created", "modified");
        updateNode = ((ObjectNode) updateNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(updateNode))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void update_shouldDenyAccessForRoleUserWithoutExplicitPermission() throws Exception {
        JsonNode updateNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("created", "modified");
        updateNode = ((ObjectNode) updateNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(updateNode))
                    .with(authentication(getMockAuthentication(this.user)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void update_shouldUpdateTheEntityForRoleUser() throws Exception {

        userInstancePermissionService.setPermission(testData.get(0), this.user, PermissionCollectionType.UPDATE);

        JsonNode updateNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("created", "modified");
        updateNode = ((ObjectNode) updateNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(updateNode))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasKey("id")));

        Optional<S> updatedEntity = repository.findById(testData.get(0).getId());
        assertNotEquals(updatedEntity.get().getCreated(), updatedEntity.get().getModified());
    }

    @Test
    public void update_shouldUpdateTheEntityForRoleAdmin() throws Exception {
        JsonNode updateNode = objectMapper.valueToTree(testData.get(0));
        List<String> fieldsToRemove = List.of("created", "modified");
        updateNode = ((ObjectNode) updateNode).remove(fieldsToRemove);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(Encoding.DEFAULT_CHARSET.toString())
                    .content(objectMapper.writeValueAsString(updateNode))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasKey("id")));

        Optional<S> updatedEntity = repository.findById(testData.get(0).getId());
        assertNotEquals(updatedEntity.get().getCreated(), updatedEntity.get().getModified());
    }

    @Test
    public void delete_shouldDenyAccessForRoleAnonymous() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        List<S> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
    public void delete_shouldDenyAccessForRoleUserWithoutExplicitPermission() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.user)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        List<S> persistedEntities = repository.findAll();
        assertEquals(3, persistedEntities.size());
    }

    @Test
    public void delete_shouldDeleteAnAvailableEntityForRoleUser() throws Exception {

        userInstancePermissionService.setPermission(testData.get(0), this.user, PermissionCollectionType.READ_DELETE);

        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.user)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<S> persistedEntities = repository.findAll();
        assertEquals(2, persistedEntities.size());
    }

    @Test
    public void delete_shouldDeleteAnAvailableEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(authentication(getMockAuthentication(this.adminUser)))
                    .with(csrf())
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<S> persistedEntities = repository.findAll();
        assertEquals(2, persistedEntities.size());
    }

}
