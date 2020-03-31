package de.terrestris.shogun.lib.exception;

public final class MailException extends RuntimeException {

    // TODO Really?!
    private static final long serialVersionUID = -199880186348214092L;

    public MailException() {
        super();
    }

    public MailException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MailException(final String message) {
        super(message);
    }

    public MailException(final Throwable cause) {
        super(cause);
    }
}
