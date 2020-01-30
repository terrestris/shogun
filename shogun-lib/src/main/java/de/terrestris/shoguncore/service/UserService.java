package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dto.RegisterUserDto;
import de.terrestris.shoguncore.enumeration.PermissionCollectionType;
import de.terrestris.shoguncore.event.OnRegistrationConfirmedEvent;
import de.terrestris.shoguncore.exception.EmailExistsException;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.UserVerificationToken;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.repository.token.UserVerificationTokenRepository;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
import de.terrestris.shoguncore.specification.UserSpecification;
import de.terrestris.shoguncore.specification.token.UserVerificationTokenSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
public class UserService extends BaseService<UserRepository, User> {

    @Autowired
    private UserVerificationTokenRepository userVerificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private UserInstancePermissionService userInstancePermissionService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final String TOKEN_INVALID = "invalidToken";
    private static final String TOKEN_EXPIRED = "expired";
    private static final String TOKEN_VALID = "valid";

    @Transactional(isolation = Isolation.SERIALIZABLE)
//    @PreAuthorize("hasAuthority('ROLE_USER_AUTH_COMPLETE') or (isAnonymous() and !#enabled)")
//    @PreAuthorize("(isAnonymous() and !#enabled)")
    public User register(RegisterUserDto registerUserData) throws IllegalArgumentException, EmailExistsException {
        if (registerUserData == null) {
            throw new IllegalArgumentException("Invalid user data");
        }

        if (emailExists(registerUserData.getEmail())) {
            LOG.info("Registration aborted because email {} already exists", registerUserData.getEmail());
            throw new EmailExistsException("There already is an account with this email address");
        }

        LOG.info("Registering a new user.");

        User user = new User();
        user.setUsername(registerUserData.getEmail());
        user.setEmail(registerUserData.getEmail());
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(registerUserData.getPassword()));

        return repository.save(user);
    }

    @PreAuthorize("isAnonymous() or permitAll()")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteUser(User user) {
        repository.delete(user);
    }

    /**
     * Checks if an username exists.
     * @param username The username to be checked.
     * @return Whether the username already exists in the database or not.
     */
    @PreAuthorize("isAnonymous() or permitAll()")
    private boolean usernameExists(final String username) {
        return this.repository.findOne(UserSpecification.findByUserName(username)).isPresent();
    }

    /**
     * Checks if an email exists.
     * @param email The email to be checked.
     * @return Whether the username already exists in the database or not.
     */
    @PreAuthorize("isAnonymous() or permitAll()")
    private boolean emailExists(final String email) {
        return this.repository.findOne(UserSpecification.findByMail(email)).isPresent();
    }

    /**
     * Creates an initial verification token for a new user.
     * @param user The user
     * @param token The token
     */
    @PreAuthorize("isAnonymous() or permitAll()")
    public void createVerificationTokenForUser(final User user, final String token) {
        final UserVerificationToken myToken = new UserVerificationToken(token, user);
        userVerificationTokenRepository.save(myToken);
    }

    /**
     * Validates the expiration date of a verification token
     * @param token The token string.
     * @return Returns `TOKEN_INVALID` if the token can't be found, returns `TOKEN_EXPIRED` if the token has expired and
     * `TOKEN_VALID` otherwise.
     */
    @PreAuthorize("isAnonymous() or permitAll()")
    public String validateVerificationToken(String token) {
        final UserVerificationToken verificationToken =
            userVerificationTokenRepository.findOne(UserVerificationTokenSpecification.findByToken(token)).orElseThrow();

        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
            .getTime()
            - cal.getTime()
            .getTime()) <= 0) {
            userVerificationTokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        enableUser(user);

        userVerificationTokenRepository.delete(verificationToken);

        OnRegistrationConfirmedEvent event = new OnRegistrationConfirmedEvent(user);
        eventPublisher.publishEvent(event);

        return TOKEN_VALID;
    }

    private void enableUser(User user) {
        user.setEnabled(true);

        identityService.initUserIdenity(user);

        userInstancePermissionService.setPermission(user, user, PermissionCollectionType.ADMIN);

        repository.save(user);
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
