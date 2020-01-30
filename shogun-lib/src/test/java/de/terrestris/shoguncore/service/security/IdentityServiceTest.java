//package de.terrestris.shoguncore.service.security;
//
//import de.terrestris.shoguncore.config.JdbcConfiguration;
//import de.terrestris.shoguncore.config.JacksonConfig;
//import de.terrestris.shoguncore.model.Group;
//import de.terrestris.shoguncore.model.Role;
//import de.terrestris.shoguncore.model.User;
//import de.terrestris.shoguncore.model.security.Identity;
//import de.terrestris.shoguncore.security.SecurityContextUtil;
//import de.terrestris.shoguncore.service.GroupService;
//import de.terrestris.shoguncore.service.RoleService;
//import de.terrestris.shoguncore.service.UserService;
//import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.transaction.Transactional;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.collection.IsIn.in;
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {
//    JacksonConfig.class,
//    JdbcConfiguration.class,
//    IdentityService.class,
//    UserService.class,
//    GroupService.class,
//    RoleService.class,
//    BCryptPasswordEncoder.class,
//    UserInstancePermissionService.class,
//    SecurityContextUtil.class
//})
//@Transactional
//public class IdentityServiceTest {
//
//    @Autowired
//    private IdentityService identityService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private GroupService groupService;
//
//    @Autowired
//    private RoleService roleService;
//
//    private User userA;
//    private User userB;
//    private User userC;
//    private Group groupA;
//    private Group groupB;
//    private Group groupC;
//    private Role roleA;
//    private Role roleB;
//    private Role roleC;
//    private Identity identityA;
//    private Identity identityB;
//    private Identity identityC;
//
//    @Before
//    public void init() {
//        userA = new User();
//        userA.setUsername("a");
//        userA.setEmail("a@a.de");
//        userB = new User();
//        userB.setUsername("b");
//        userB.setEmail("b@b.de");
//        userC = new User();
//        userC.setUsername("c");
//        userC.setEmail("c@c.de");
//
//        groupA = new Group();
//        groupA.setName("a");
//        groupB = new Group();
//        groupB.setName("b");
//        groupC = new Group();
//        groupC.setName("c");
//
//        roleA = new Role();
//        roleA.setName("a");
//        roleB = new Role();
//        roleB.setName("b");
//        roleC = new Role();
//        roleC.setName("c");
//
//        identityA = new Identity();
//        identityB = new Identity();
//        identityC = new Identity();
//
//        identityA.setUser(userA);
//        identityA.setGroup(groupA);
//        identityA.setRole(roleA);
//
//        identityB.setUser(userB);
//        identityB.setGroup(groupB);
//        identityB.setRole(roleB);
//
//        identityC.setUser(userC);
//        identityC.setGroup(groupC);
//        identityC.setRole(roleC);
//
//        userService.create(userA);
//        userService.create(userB);
//        userService.create(userC);
//
//        groupService.create(groupA);
//        groupService.create(groupB);
//        groupService.create(groupC);
//
//        roleService.create(roleA);
//        roleService.create(roleB);
//        roleService.create(roleC);
//
//        identityService.create(identityA);
//        identityService.create(identityB);
//        identityService.create(identityC);
//    }
//
//    @Test
//    public void findAllIdentitiesBy_shouldReturnIdentityByUser() {
//        List<Identity> foundIdentities = identityService.findAllIdentitiesBy(userA);
//
//        assertThat(foundIdentities.size(), is(1));
//        assertThat(identityA, in(foundIdentities));
//    }
//
//    @Test
//    public void findAllIdentitiesBy_shouldReturnIdentityByGroup() {
//        List<Identity> foundIdentities = identityService.findAllIdentitiesBy(groupA);
//
//        assertThat(foundIdentities.size(), is(1));
//        assertThat(identityA, in(foundIdentities));
//    }
//
//    @Test
//    public void findAllIdentitiesBy_shouldReturnIdentityByRole() {
//        List<Identity> foundIdentities = identityService.findAllIdentitiesBy(roleA);
//
//        assertThat(foundIdentities.size(), is(1));
//        assertThat(identityA, in(foundIdentities));
//    }
//}
