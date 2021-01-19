package de.terrestris.shoguncore.enumeration;

public enum PermissionCollectionType {
    CREATE("CREATE"),
    READ("READ"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),

    CREATE_READ("CREATE_READ"),
    CREATE_UPDATE("CREATE_UPDATE"),
    CREATE_DELETE("CREATE_DELETE"),
    READ_UPDATE("READ_UPDATE"),
    READ_DELETE("READ_DELETE"),
    UPDATE_DELETE("UPDATE_DELETE"),

    CREATE_READ_UPDATE("CREATE_READ_UPDATE"),
    CREATE_READ_DELETE("CREATE_READ_DELETE"),
    CREATE_UPDATE_DELETE("CREATE_UPDATE_DELETE"),
    READ_UPDATE_DELETE("READ_UPDATE_DELETE"),

    ADMIN("ADMIN");

    private final String type;

    private PermissionCollectionType(String type) {
        this.type = type;
    }
}
