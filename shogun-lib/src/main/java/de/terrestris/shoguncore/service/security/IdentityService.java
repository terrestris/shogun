package de.terrestris.shoguncore.service.security;

import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.Identity;
import de.terrestris.shoguncore.repository.RoleRepository;
import de.terrestris.shoguncore.repository.security.IdentityRepository;
import de.terrestris.shoguncore.service.BaseService;
import de.terrestris.shoguncore.specification.IdentitySpecifications;
import de.terrestris.shoguncore.specification.RoleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// TODO add security annotations if needed
@Service
public class IdentityService extends BaseService<IdentityRepository, Identity> {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAllRolesFrom(User user) {
        return this.findAllIdentitiesBy(user).stream().map(
                identity -> identity.getRole()).collect(Collectors.toList());
    }

    public List<Group> findAllGroupsFrom(User user) {
        return this.findAllIdentitiesBy(user).stream().map(
                identity -> identity.getGroup()).collect(Collectors.toList());
    }

    public List<User> findAllMembersOf(Group group) {
        return this.findAllIdentitiesBy(group).stream().map(
                identity -> identity.getUser()).collect(Collectors.toList());
    }

    public List<Identity> findAllIdentitiesBy(User user) {
        return repository.findAll(
                IdentitySpecifications.hasUser(user)
        );
    }

    public List<Identity> findAllIdentitiesBy(Group group) {
        return repository.findAll(
                IdentitySpecifications.hasGroup(group)
        );
    }

    public List<Identity> findAllIdentitiesBy(Role role) {
        return repository.findAll(
                IdentitySpecifications.hasRole(role)
        );
    }

    public List<Identity> findAllIdentitiesBy(User user, Group group) {
        return repository.findAll(Specification.where(
                IdentitySpecifications.hasUser(user)).and(
                IdentitySpecifications.hasGroup(group)
        ));
    }

    public List<Identity> findAllIdentitiesBy(User user, Group group, Role role) {
        return repository.findAll(Specification.where(
                IdentitySpecifications.hasUser(user)).and(
                IdentitySpecifications.hasGroup(group)).and(
                IdentitySpecifications.hasRole(role)
        ));
    }

    public boolean isUserMemberInGroup(User user, Group group) {
        return repository.count(Specification.where(
                IdentitySpecifications.hasUser(user)).and(
                IdentitySpecifications.hasGroup(group)
        )) > 0;
    }

    public boolean hasUserRoleInGroup(User user, Group group, Role role) {
        return repository.count(Specification.where(
                IdentitySpecifications.hasUser(user)).and(
                IdentitySpecifications.hasGroup(group)).and(
                IdentitySpecifications.hasRole(role)
        )) > 0;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addUserToGroup(User user, Group group, Role role) {

        if (this.isUserMemberInGroup(user, group)) {
            LOG.trace("User with ID " + user.getId() + " is already a member of "
                    + "group with ID " + group.getId());
            return;
        }

        Identity identity = new Identity(user, group, role);

        repository.save(identity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeUserFromGroup(User user, Group group) {

        if (this.isUserMemberInGroup(user, group) == false) {
            LOG.trace("User with ID " + user.getId() + " is not a member of "
                    + "group with ID " + group.getId());
            return;
        }

        List<Identity> identities = this.findAllIdentitiesBy(user, group);

        for (Identity identity : identities) {
            repository.delete(identity);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Identity initUserIdenity(User user) {
        Role userRole = roleRepository.findOne(RoleSpecification.findByName("ROLE_USER")).orElseThrow();

        Identity identity = new Identity(user, null, userRole);
        return repository.save(identity);
    }

    // TODO Check if needed
//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public void removeUserPermissionsFromGroup(User user, Group userGroup) {
//        if (this.isUserMemberInUserGroup(user, userGroup) == false) {
//            LOG.trace("User with ID " + user.getId() + " is not a member of "
//                    + "group with ID " + userGroup.getId());
//            return;
//        }
//
//        List<Identity> userGroupRoles = this.findIdentitiesBy(user, userGroup);
//
//        for (Identity userGroupRole : userGroupRoles) {
//            removeAndSaveUserPermissions((E) userGroupRole, (User) user, Permission.ADMIN);
//        }
//    }

}
