package de.terrestris.shogun.lib.event;

import de.terrestris.shogun.lib.model.User;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationConfirmedEvent extends ApplicationEvent {

    private final User user;

    public OnRegistrationConfirmedEvent(final User user) {
        super(user);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}

