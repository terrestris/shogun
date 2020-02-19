package de.terrestris.shogun.lib.exception;

public final class EmailExistsException extends RuntimeException {

    // TODO Really?!
    private static final long serialVersionUID = -199880186348214092L;

    public EmailExistsException() {
        super();
    }

    public EmailExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailExistsException(final String message) {
        super(message);
    }

    public EmailExistsException(final Throwable cause) {
        super(cause);
    }
}

