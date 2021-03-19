package de.terrestris.shoguncore.listener;

import de.terrestris.shoguncore.event.OnRegistrationCompleteEvent;
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
public class RegistrationListener {

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
    public void handleRegistrationEvent(OnRegistrationCompleteEvent event) {
        this.handleConfirmRegistration(event);
    }

    private void handleConfirmRegistration(final OnRegistrationCompleteEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);

        LOG.info("Generated verification token for user " + user.getUsername());

        try {
            mailService.sendRegistrationEmailMessage(event.getAppUrl(), user, token, event.getLocale());

        } catch (Exception e) {
            throw new MailException(e.getMessage());
        }

        LOG.info("Successfully sent registration mail to user " + user.getEmail());
    }
}
