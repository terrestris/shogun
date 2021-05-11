package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Locale;
import java.util.Objects;

@Service
public class MailService {
    @Autowired
    protected MessageSource messageSource;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    /**
     * Generates a simple email to send the confirmation the user has reset the password, with the new password
     * @param user
     * @param password
     * @param locale
     */
    public void sendPasswordResetConfirmedEmailMessage(final User user, final String password, Locale locale) {
        final String recipientAddress = user.getEmail();
        final String subject = messageSource.getMessage("passwordReset.email.confirmed.subject", null, locale);

        String message = String.format("%s %s,%n%n",
            messageSource.getMessage("passwordReset.email.confirmed.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messageSource.getMessage("passwordReset.email.confirmed.infoSuccess", null, locale) + " ";
        message += messageSource.getMessage("passwordReset.email.confirmed.newPassword", null, locale);
        message += password;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        mailSender.send(email);
    }

    public void sendPasswordChangeConfirmedEmailMessage(final User user, Locale locale) {
        final String recipientAddress = user.getEmail();
        final String subject = messageSource.getMessage("passwordChange.email.confirmed.subject", null, locale);

        String message = String.format("%s %s,%n%n",
            messageSource.getMessage("passwordChange.email.confirmed.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messageSource.getMessage("passwordChange.email.confirmed.infoSuccess", null, locale);

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        mailSender.send(email);
    }

    /**
     * Constructs a Registration Confirm Email based on the users' properties (name, language, etc.)
     * for a given application URL
     *
     * @param appUrl Base URL of the application. Must be provided without a trailing slash. The confirm registration link is
     *              built from this URL.
     * @param user The user about to be registered.
     * @param token The unique token (e.g. UUID).
     */
    public void sendRegistrationEmailMessage(String appUrl, final User user, final String token, Locale locale)
            throws MessagingException {
        final String recipientAddress = user.getEmail();
        final String subject = messageSource.getMessage("registration.email.subject", null, locale);
        final String confirmationUrl = appUrl + "/users/confirm?token=" + token;

        String message = String.format("%s %s,%n%n",
            messageSource.getMessage("registration.email.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messageSource.getMessage("registration.email.infoSuccess", null, locale) + " ";
        message += messageSource.getMessage("registration.email.linkText", null, locale);
        message +=  " \r\n\r\n" + confirmationUrl;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        mailSender.send(email);
    }

    /**
     * Constructs a reset password confirm email based on the users' properties (name, language, etc.)
     * for a given application URL
     *
     * @param appUrl Base URL of the application. Must be provided without a trailing slash. The confirm registration link is
     *              built from this URL.
     * @param user The user about to be registered.
     * @param token The unique token (e.g. UUID).
     */
    public void sendPasswordResetEmailMessage(String appUrl, final User user, final String token, Locale locale)
            throws MessagingException {
        final String recipientAddress = user.getEmail();
        final String subject = messageSource.getMessage("passwordReset.email.subject", null, locale);
        final String confirmationUrl = appUrl + "/users/password/resetPassword/confirm?token=" + token;

        String message = String.format("%s %s,%n%n",
            messageSource.getMessage("passwordReset.email.salutationPrefix", null, locale),
            user.getUsername()
        );
        message += messageSource.getMessage("passwordReset.email.infoSuccess", null, locale) + " ";
        message += messageSource.getMessage("passwordReset.email.linkText", null, locale);
        message +=  " \r\n\r\n" + confirmationUrl;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email"), "Environment variable support.email not set"));

        mailSender.send(email);
    }
}
