package de.terrestris.shoguncore.enumeration;

public enum PermissionCollectionType {
    CREATE("CREATE"),
    CREATE_READ("CREATE_READ"),
    CREATE_READ_UPDATE("CREATE_READ_UPDATE"),
    CREATE_READ_DELETE("CREATE_READ_DELETE"),
    CREATE_UPDATE("CREATE_UPDATE"),
    CREATE_UPDATE_DELETE("CREATE_UPDATE_DELETE"),
    CREATE_DELETE("CREATE_DELETE"),
    READ("READ"),
    READ_UPDATE("READ_UPDATE"),
    READ_DELETE("READ_DELETE"),
    UPDATE("UPDATE"),
    UPDATE_DELETE("UPDATE_DELETE"),
    DELETE("DELETE"),
    ADMIN("ADMIN");

    private final String type;

    private PermissionCollectionType(String type) {
        this.type = type;
    }
}
