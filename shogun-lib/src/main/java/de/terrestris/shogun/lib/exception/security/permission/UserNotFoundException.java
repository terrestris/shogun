package de.terrestris.shogun.lib.exception.security.permission;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
public final class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(Long userId, String message) {
        super(HttpStatus.NOT_FOUND, message);

        log.error("Could not find user with ID {}", userId);
    }

    public UserNotFoundException(Long userId, MessageSource messageSource) {
        this(
            userId,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }
}

