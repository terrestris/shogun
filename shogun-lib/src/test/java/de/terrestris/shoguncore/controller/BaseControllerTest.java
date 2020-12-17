package de.terrestris.shoguncore.controller;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.config.JacksonConfig;
import de.terrestris.shoguncore.config.JdbcConfiguration;
import de.terrestris.shoguncore.config.MethodSecurityConfig;
import de.terrestris.shoguncore.config.WebConfig;
import de.terrestris.shoguncore.config.WebSecurityConfig;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.repository.security.permission.PermissionCollectionRepository;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.security.access.BasePermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shoguncore.service.ApplicationService;
import de.terrestris.shoguncore.service.FileService;
import de.terrestris.shoguncore.service.GroupService;
import de.terrestris.shoguncore.service.ImageFileService;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.service.RoleService;
import de.terrestris.shoguncore.service.UserService;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.service.security.permission.GroupClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shoguncore.service.security.permission.UserClassPermissionService;
import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
import de.terrestris.shoguncore.specification.UserSpecification;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    JacksonConfig.class,
    JdbcConfiguration.class,
    WebSecurityConfig.class,
    WebConfig.class,
    MethodSecurityConfig.class,
    ApplicationService.class,
    UserInstancePermissionService.class,
    GroupInstancePermissionService.class,
    UserClassPermissionService.class,
    GroupClassPermissionService.class,
    SecurityContextUtil.class,
    IdentityService.class,
    FileService.class,
    GroupService.class,
    ImageFileService.class,
    LayerService.class,
    RoleService.class,
    UserService.class,
    BCryptPasswordEncoder.class,
    BasePermissionEvaluator.class,
    DefaultPermissionEvaluator.class
})
@WebAppConfiguration
public abstract class BaseControllerTest<U extends BaseController, R extends BaseCrudRepository, S extends BaseEntity> implements IBaseController {

    protected Class<S> entityClass;

    @Autowired
    protected U controller;

    @Autowired
    protected R repository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PermissionCollectionRepository repo;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    protected String basePath;

    protected List<S> testData;

    @Before
    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .alwaysDo(print())
            .apply(springSecurity())
            .build();
    }

    @BeforeTestClass()
    public void initAdminUser() {
        User adminUser = new User();
        adminUser.setEmail("admin");
        adminUser.setUsername("admin");
        adminUser.setEnabled(true);

        userRepository.save(adminUser);
    }

    @AfterTestClass()
    public void deinitAdminUser() {
        Optional<User> adminUser = userRepository.findOne(
            UserSpecification.findByMail("admin"));

        userRepository.save(adminUser.get());
    }

    @Test
    public void findAll_shouldReturnAllAvailableEntitiesForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(basePath)
                    .with(user("admin").password("pass").roles("ADMIN"))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void findOne_shouldReturnAnAvailableEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(user("admin").password("pass").roles("ADMIN"))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasEntry("id", testData.get(0).getId().intValue())));
    }

//    @Test
//    public void add_shouldCreateTheEntityForRoleAdmin() throws Exception {
//        Role entityToSend = new Role();
//        entityToSend.setName("Peter");
//
//        this.mockMvc
//            .perform(
//                MockMvcRequestBuilders
//                    .post(String.format("%s", basePath))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(entityToSend))
//                    .with(user("admin").password("pass").roles("ADMIN"))
//            )
//            .andExpect(MockMvcResultMatchers.status().isCreated())
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$").exists())
//            .andExpect(jsonPath("$", hasKey("id")));
//    }

    @Test
    public void update_shouldUpdateTheEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testData.get(0)))
                    .with(user("admin").password("pass").roles("ADMIN"))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$", hasKey("id")));
    }

    @Test
    public void delete_shouldDeleteAnAvailableEntityForRoleAdmin() throws Exception {
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete(String.format("%s/%s", basePath, testData.get(0).getId()))
                    .with(user("admin").password("pass").roles("ADMIN"))
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}
