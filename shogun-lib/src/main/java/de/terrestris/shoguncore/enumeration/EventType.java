package de.terrestris.shoguncore.enumeration;

public enum EventType {
    REQUEST("REQUEST"),
    RESPONSE("RESPONSE");

    private final String type;

    private EventType(String type) {
        this.type = type;
    }
}
