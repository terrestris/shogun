package de.terrestris.shogun.lib.enumeration;

public enum PermissionType {
    ADMIN("ADMIN"),
    CREATE("CREATE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    READ("READ");

    private final String type;

    private PermissionType(String type) {
        this.type = type;
    }
}
