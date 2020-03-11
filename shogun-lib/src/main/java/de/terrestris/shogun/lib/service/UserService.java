package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService<UserRepository, User> {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteUser(User user) {
        repository.delete(user);
    }

//    @Transactional(readOnly = true)
//    public Optional<User> getUserBySession() {
//
//        final Object principal = SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        if (!(principal instanceof User)) {
//            return Optional.empty();
//        }
//
//        User loggedInUser = (User) principal;
//
//        // The SecurityContextHolder holds a static copy of the user from
//        // the moment he logged in. So we need to get the current instance from
//        // the persistence level.
//        Long id = loggedInUser.getId();
//
//        return repository.findById(id);
//    }
}
