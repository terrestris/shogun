package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.dto.PasswordChange;
import de.terrestris.shoguncore.dto.PasswordReset;
import de.terrestris.shoguncore.dto.RegisterUserDto;
import de.terrestris.shoguncore.event.OnPasswordResetCompleteEvent;
import de.terrestris.shoguncore.event.OnPasswordResetRequestEvent;
import de.terrestris.shoguncore.event.OnRegistrationCompleteEvent;
import de.terrestris.shoguncore.exception.EmailExistsException;
import de.terrestris.shoguncore.exception.MailException;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.UserVerificationToken;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.service.UserService;
import de.terrestris.shoguncore.specification.token.UserVerificationTokenSpecification;
import de.terrestris.shoguncore.util.HttpUtil;
import de.terrestris.shoguncore.util.ValidationUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import static de.terrestris.shoguncore.util.HttpUtil.getApplicationURIFromRequest;

@RestController
@RequestMapping("/users")
@ConditionalOnExpression("${controller.users.enabled:true}")
public class UserController extends BaseController<UserService, User> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity register(RegisterUserDto registerUserData, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ValidationUtil.validateBindingResult(bindingResult);
        }

        LOG.info("Registering user {}", registerUserData.getEmail());

        User newUser = null;
        try {
            newUser = this.service.register(registerUserData);
            LOG.info("Successfully created user {}", registerUserData.getEmail());
        } catch (EmailExistsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("registration.error.emailExists", null, request.getLocale())
            );
        } catch (Exception ex) {
            LOG.error("Error registering user {} {}", registerUserData.getEmail(), ex.getMessage());
            LOG.trace("Full stack trace: ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("registration.error", null, request.getLocale())
            );
        }

        // Publish event which sends a confirmation email to enable the user
        try {
            URI appUrl = HttpUtil.getApplicationURIFromRequest(request);
            OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent(newUser, request.getLocale(), appUrl.toString());
            eventPublisher.publishEvent(event);
        } catch (MailException e) {
            LOG.error("Error sending mail: {}", e.getMessage());
            LOG.trace("Full stack trace: ", e);

            this.service.deleteUser(newUser);

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("registration.error.mail", null, request.getLocale())
            );
        } catch (Exception ex) {
            LOG.error("Error publishing registration event: {}", ex.getMessage());
            LOG.trace("Full stack trace: ", ex);

            this.service.deleteUser(newUser);

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("registration.verification.error", null, request.getLocale())
            );
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/confirm")
    public RedirectView confirmRegistration(@RequestParam("token") String token, HttpServletRequest request) {
        RedirectView redirectView = new RedirectView();

        try {
            final String result = service.validateVerificationToken(token, request.getLocale(), "userRegistration");
            if (result.equals("valid")) {
                redirectView.setUrl(HttpUtil.getApplicationURIFromRequest(request) +
                    "/login?activationSucceeded");
                LOG.info("Registration complete for token {}", token);
            } else {
                redirectView.setUrl(HttpUtil.getApplicationURIFromRequest(request) +
                    "/login?activationFailed");
                LOG.error("Registration failed: {}", result);
            }
        } catch (URISyntaxException e) {
            LOG.error("Registration failed: {}", e.getMessage());
        }

        return redirectView;
    }

    @PostMapping(value = "/password/change")
    public void changePassword(@RequestBody PasswordChange passwordChangeBody) {
        Optional<User> user = securityContextUtil.getUserBySession();

        if (user.isPresent()) {
            try {
                service.changeUserPassword(user.get(), passwordChangeBody);
            } catch(SecurityException exception) {
                LOG.debug("Your current password does not match with the given old one. Aborting password change.");

                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(
                        "password.change.OLD_PASSWORD_DOES_NOT_MATCH_ERROR",
                        null,
                        LocaleContextHolder.getLocale()
                    )
                );
            } catch(Exception exception) {
                LOG.debug("Some error occurred while updating the user password.");
                LOG.trace("Full stack trace: ", exception);

                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage(
                        "password.change.INTERNAL_SERVER_ERROR",
                        null,
                        LocaleContextHolder.getLocale()
                    )
                );
            }
        } else {
            LOG.debug("User is not logged in.");

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage(
                    "password.change.USER_NOT_FOUND_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                )
            );
        }
    }

    @PostMapping(value = "/resetPassword")
    public void resetPassword(@RequestBody PasswordReset passwordResetBody, HttpServletRequest request) {
        User user = service.getUserByEmail(passwordResetBody.getEmail());

        // publish event that sends the email with link to confirm password change
        // Publish event which sends a confirmation email to enable the user
        try {
            URI appUrl = HttpUtil.getApplicationURIFromRequest(request);
            OnPasswordResetRequestEvent event = new OnPasswordResetRequestEvent(user, request.getLocale(), appUrl.toString());
            eventPublisher.publishEvent(event);
        } catch (MailException | URISyntaxException e) {
            LOG.error("Error sending mail: {}", e.getMessage());
            LOG.trace("Full stack trace: ", e);

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("registration.error.mail", null, request.getLocale())
            );
        }
    }

    @GetMapping(value = "/password/resetPassword/confirm")
    public RedirectView confirmPasswordReset(@RequestParam("token") String token, HttpServletRequest request) throws URISyntaxException {
        RedirectView redirectView = new RedirectView();
        try {
            final String result = service.validateVerificationToken(token, request.getLocale(), "passwordReset");
            if (result.equals("valid")) {
                redirectView.setUrl(String.format("%s/login?passwordResetSuccess", getApplicationURIFromRequest(request)));
                LOG.info("Allowed password recovery for token {}, redirecting.", token);
            } else {
                redirectView.setUrl(String.format("%s/login?passwordResetFailed", getApplicationURIFromRequest(request)));
                LOG.error("Password recovery not allowed. Process failed: {}", result);
            }
        } catch (URISyntaxException e) {
            redirectView.setUrl(String.format("%s/login?passwordResetFailed", getApplicationURIFromRequest(request)));
            LOG.error("Password recovery failure: {}", e.getMessage());
        }
        return redirectView;
    }
}
