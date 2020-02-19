package de.terrestris.shogun.lib.enumeration;

public enum LayerType {
    TILEWMS("TileWMS"),
    VECTORTILE("VectorTile"),
    WFS("WFS"),
    WMS("WMS"),
    WMTS("WMTS"),
    XYZ("XYZ");

    private final String type;

    LayerType(String type) {
        this.type = type;
    }
}
