package de.terrestris.shogun.interceptor.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InterceptorException extends Exception {

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
