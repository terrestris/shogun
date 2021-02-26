package de.terrestris.shogun.lib.exception.security.permission;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
public final class CreatePermissionException extends ResponseStatusException {

    public CreatePermissionException(Exception e, String message) {
        super(HttpStatus.NOT_FOUND, message);

        log.error("Error while setting the permission: {}", e.getMessage());
        log.trace("Full stack trace: ", e);
    }

    public CreatePermissionException(Exception e, MessageSource messageSource) {
        this(
            e,
            messageSource.getMessage(
                "BaseController.INTERNAL_SERVER_ERROR",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }
}

