package de.terrestris.shoguncore.event;

import de.terrestris.shoguncore.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnPasswordResetCompleteEvent extends ApplicationEvent {

    private final User user;
    private final Locale locale;

    public OnPasswordResetCompleteEvent(final User user, final Locale locale) {
        super(user);
        this.user = user;
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }
    public Locale getLocale() {
        return locale;
    }

}

