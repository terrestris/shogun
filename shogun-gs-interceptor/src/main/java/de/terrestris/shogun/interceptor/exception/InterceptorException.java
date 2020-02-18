package de.terrestris.shogun.interceptor.exception;

public class InterceptorException extends Exception {

    private static final long serialVersionUID = 1L;

    public InterceptorException() {
    }

    public InterceptorException(String message) {
        super(message);
    }

    public InterceptorException(Throwable cause) {
        super(cause);
    }

    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

}
