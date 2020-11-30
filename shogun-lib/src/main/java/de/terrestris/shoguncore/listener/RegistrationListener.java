package de.terrestris.shoguncore.listener;

import de.terrestris.shoguncore.event.OnRegistrationCompleteEvent;
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
public class RegistrationListener {

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
    public void handleRegistrationEvent(OnRegistrationCompleteEvent event) {
        this.handleConfirmRegistration(event);
    }

    private void handleConfirmRegistration(final OnRegistrationCompleteEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);

        LOG.info("Generated verification token for user " + user.getUsername());

        // TODO Fix me
        try {
            final SimpleMailMessage email = constructRegistrationEmailMessage(event.getAppUrl(), user, token, event.getLocale());
            mailSender.send(email);
        } catch (Exception e) {
            throw new MailException(e.getMessage());
        }

        LOG.info("Successfully sent registration mail to user " + user.getEmail());
    }

    /**
     * Constructs a Registration Confirm Email based on the users' properties (name, language, etc.)
     * for a given application URL
     *
     * @param appUrl Base URL of the application. Must be provided without a trailing slash. The confirm registration link is
     *              built from this URL.
     * @param user The user about to be registered.
     * @param token The unique token (e.g. UUID).
     * @return A SimpleMailMessage translated depending on the users' language containing the specified text and a
     * registration link.
     */
    private SimpleMailMessage constructRegistrationEmailMessage(String appUrl, final User user,
                                                                final String token, Locale locale) throws MessagingException {
        final String recipientAddress = user.getEmail();
        final String subject = messages.getMessage("registration.email.subject", null, locale);
        final String confirmationUrl = appUrl + "/users/confirm?token=" + token;

        String message = String.format("%s %s,%n%n",
            messages.getMessage("registration.email.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messages.getMessage("registration.email.infoSuccess", null, locale) + " ";
        message += messages.getMessage("registration.email.linkText", null, locale);
        message +=  " \r\n\r\n" + confirmationUrl;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        return email;
    }

}
