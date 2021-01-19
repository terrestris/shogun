package de.terrestris.shoguncore.listener;

import de.terrestris.shoguncore.event.OnPasswordResetCompleteEvent;
import de.terrestris.shoguncore.event.OnPasswordResetRequestEvent;
import de.terrestris.shoguncore.exception.MailException;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Component
public class PasswordResetListener {

    private Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @EventListener
    public void handlePasswordResetEvent(OnPasswordResetRequestEvent event) {
        this.handleConfirmPasswordReset(event);
    }

    private void handleConfirmPasswordReset(final OnPasswordResetRequestEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);

        LOG.info("Generated verification token for user {}", user.getUsername());

        try {
            final SimpleMailMessage email = constructPasswordResetEmailMessage(event.getAppUrl(), user, token, event.getLocale());
            mailSender.send(email);
        } catch (Exception e) {
            throw new MailException(e.getMessage());
        }

        LOG.info("Successfully sent password reset mail to user " + user.getEmail());
    }

    /**
     * Constructs a Reset password Confirm Email based on the users' properties (name, language, etc.)
     * for a given application URL
     *
     * @param appUrl Base URL of the application. Must be provided without a trailing slash. The confirm registration link is
     *              built from this URL.
     * @param user The user about to be registered.
     * @param token The unique token (e.g. UUID).
     * @return A SimpleMailMessage translated depending on the users' language containing the specified text and a
     * registration link.
     */
    private SimpleMailMessage constructPasswordResetEmailMessage(String appUrl, final User user,
                                                                final String token, Locale locale) throws MessagingException {
        final String recipientAddress = user.getEmail();
        final String subject = messages.getMessage("passwordReset.email.subject", null, locale);
        final String confirmationUrl = appUrl + "/users/password/resetPassword/confirm?token=" + token;

        String message = String.format("%s %s,%n%n",
            messages.getMessage("passwordReset.email.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messages.getMessage("passwordReset.email.infoSuccess", null, locale) + " ";
        message += messages.getMessage("passwordReset.email.linkText", null, locale);
        message +=  " \r\n\r\n" + confirmationUrl;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        return email;
    }

}
