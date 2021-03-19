package de.terrestris.shoguncore.listener;

import de.terrestris.shoguncore.event.OnPasswordResetRequestEvent;
import de.terrestris.shoguncore.exception.MailException;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.MailService;
import de.terrestris.shoguncore.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordResetListener {

    private Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;

    @Autowired
    private MailService mailService;

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
            mailService.sendPasswordResetEmailMessage(event.getAppUrl(), user, token, event.getLocale());
        } catch (Exception e) {
            throw new MailException(e.getMessage());
        }

        LOG.info("Successfully sent password reset mail to user {}", user.getEmail());
    }



}
