package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.specification.UserSpecification;
import de.terrestris.shogun.lib.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class ShogunUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param email the email of the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            final Optional<User> user = userRepository.findOne(UserSpecification.findByMail(email));

            if (user.isEmpty()) {
                throw new UsernameNotFoundException("No user found with email: " + email);
            }

            List<GrantedAuthority> grantedAuthorities = securityContextUtil.getGrantedAuthorities(user.get());

            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(), user.get().getPassword(), true,
                    true, true, true, grantedAuthorities);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
