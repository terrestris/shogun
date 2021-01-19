package de.terrestris.shoguncore.enumeration;

public enum UserVerificationTokenType {
    PASSWORD_RESET("passwordReset"),
    USER_REGISTRATION("userRegistration");

    private final String type;

    private UserVerificationTokenType(String type) {
        this.type = type;
    }
}
