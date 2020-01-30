package de.terrestris.shoguncore.enumeration;

public enum ServiceType {
    WMS("WMS"),
    WFS("WFS"),
    WCS("WCS"),
    WPS("WPS"),
    W3DS("W3DS");

    private final String type;

    private ServiceType(String type) {
        this.type = type;
    }
}
